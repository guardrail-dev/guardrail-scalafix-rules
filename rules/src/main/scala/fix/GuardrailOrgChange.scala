package fix

import java.util.Locale
import scalafix.v1._
import scala.meta._

class GuardrailOrgChange extends SemanticRule("GuardrailOrgChange") {
  def rewrite: Term.Ref => Term.Ref = {
    case q"com.twilio.guardrail"  => q"dev.guardrail"
    case Term.Select(inner: Term.Ref, next) => Term.Select(rewrite(inner), next)
    case other                    => other
  }

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect({
      case term @ Import(xs) =>
        val replacement = Import(xs.map {
          case Importer(term, fields) => Importer(rewrite(term), fields)
        })

        Patch.replaceTree(term, replacement.syntax)
    }).asPatch
  }
}
