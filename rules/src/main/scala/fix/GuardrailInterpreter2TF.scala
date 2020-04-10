package fix

import scalafix.v1._
import scala.meta._

class GuardrailInterpreter2TF extends SemanticRule("GuardrailInterpreter2TF") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // Find an object where...
    doc.tree.collect { case Defn.Object(_, _, Template(_, _, _, stats)) =>
      // there is an apply where the body is a Match where...
      stats.collect { case Defn.Def(_, Term.Name("apply"), _, _, _, Term.Match(_, cases)) =>
        // the cases are Pat.Extract where...
        cases.collect {
          case Case(extract@Pat.Extract(Term.Name(name), args), None, body) =>
            val methodName = name.take(1).toLowerCase ++ name.drop(1)
            // there is a Defn.Def named the lower-case equivalent of the match.
            stats.collect { case method@Defn.Def(_, Term.Name(`methodName`), _, paramss, _, Term.Name("???")) =>
              val (newPatArgs, newArgs, (newMethodArgs, extractions)): (List[Pat.Var], List[Term], (List[Term.Param], List[Stat])) =
                args.zip(paramss.take(1).flatten).map({
                  case (pat, param@Term.Param(_, pname@Term.Name(_), _, _)) =>
                    val (customName, extraction) = pat match {
                      case Pat.Var(a) => (a, None)
                      case pat@Pat.Extract(_, _) => (pname, Some(Defn.Val(Nil, List(pat), None, pname)))
                      case other =>
                        println(s"Unknown Pat: ${other} (${other.getClass}), falling back to $pname")
                        (pname, None)
                    }
                    (Pat.Var(pname), pname, (param.copy(name=customName), extraction))
                }).unzip3 match { case (a, b, cd) =>
                  val (c, d) = cd.unzip
                  (a, b, (c, d.flatten))
                }

              val newBody = if (extractions.nonEmpty) {
                Term.Block(extractions :+ body)
              } else body

              val patchExtract = Patch.replaceTree(extract, Pat.Extract(Term.Name(name), newPatArgs).syntax)
              val patchCaseBody = Patch.replaceTree(body, q"${Term.Name(methodName)}(..$newArgs)".syntax)
              println(s"Replacing $methodName with $newMethodArgs")
              val patchMethod = Patch.replaceTree(method, method.copy(paramss=List(newMethodArgs), body=newBody).syntax)
              patchExtract + patchCaseBody + patchMethod
            }
        }
      }
    }.flatten.flatten.flatten.asPatch
  }

}
