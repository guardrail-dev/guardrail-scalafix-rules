package fix

import java.util.Locale
import scalafix.v1._
import scala.meta._

class GuardrailIteratorToVector extends SemanticRule("GuardrailIteratorToVector") {
  def extract(matchesRespondType: PartialFunction[Tree, Boolean])(implicit doc: SemanticDocument): PartialFunction[Tree, Seq[Patch]] = {
    case defn @ Defn.Def(
      _,
      _,
      _,
      List(Term.Param(
        Nil,
        Term.Name("respond"),
        Some(respondType), None
      )) :: otherArgs,
      _,
      _
    ) if matchesRespondType.applyOrElse(respondType, Function.const(false) _) =>
      val result = otherArgs.flatMap { params =>
        params.flatMap {
          case param@Term.Param(mod, name, tpe, default) =>
            tpe.toSeq.flatMap(_.collect({
              case Type.Apply(tpe@t"Iterator", inner) =>
                Patch.replaceTree(tpe, t"Vector".syntax)
            }))
        }
      }

      if (result.nonEmpty) {
        println(s"Replacing Iterator with Vector in ${defn.copy(body = q"???")}")
      }

      result
  }
  override def fix(implicit doc: SemanticDocument): Patch = {
    val patterns = List(
      extract({ case Type.Singleton(Term.Select(resourceType, Term.Name(responseType))) => true }),
      extract({ case Type.Singleton(Term.Name(responseType)) => true })
    )

    val func = patterns.foldLeft[PartialFunction[Tree, Seq[Patch]]](PartialFunction.empty)(_.orElse(_))
    doc.tree.collect(func).flatten.asPatch
  }
}
