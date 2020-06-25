package fix

import java.util.Locale
import scalafix.v1._
import scala.meta._

class GuardrailScalaResponseTypes extends SemanticRule("GuardrailScalaResponseTypes") {
  private def cap(s: String): String = s.substring(0, 1).toUpperCase(Locale.US) + s.substring(1)

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect({
      case defn @ Defn.Def(
        _,
        _,
        _,
        List(Term.Param(
          Nil,
          Term.Name("respond"),
          Some(Type.Singleton(Term.Select(resourceType, responseTypeTree@Term.Name(responseType)))), None
        )) :: otherArgs,
        Some(Type.Apply(
          returnTypeName,
          List(Type.Select(returnResourceType, returnResponseTypeTree@Type.Name(returnResponseType)))
        )),
        _
      ) =>
        List(
          Patch.replaceTree(responseTypeTree, Term.Name(cap(responseType)).toString()),
          Patch.replaceTree(returnResponseTypeTree, Type.Name(cap(returnResponseType)).toString()),
        )
    }).flatten.asPatch
  }
}
