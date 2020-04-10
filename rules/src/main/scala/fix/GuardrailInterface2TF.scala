package fix

import scalafix.v1._
import scala.meta._

class GuardrailInterface2TF extends SemanticRule("GuardrailInterface2TF") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    (doc.tree.collect { case target@q"class ${Type.Name(name)}[..$interfaceTargs](implicit $inj: InjectK[..$_]) { ..$body }" =>
      doc.tree.collect { case companion@q"object ${Term.Name(`name`)} { ${defnDef@Defn.Def(_, _, _, _, Some(Type.Apply(clsType, List(l@Type.Name("L"), f))), Term.New(classInit))} }" =>

        val newInitTpe = classInit.tpe match {
          case Type.Apply(cls, List(l, f)) => Type.Apply(cls, List(l, t"Free[$f, ?]"))
          case other => println(s"Unimplemented for $other"); ???
        }

        val monadDefn = q"def MonadF = Free.catsFreeMonadForFree"
        val patchInterpDefn = Patch.replaceTree(defnDef, defnDef.copy(
          body=q"new ${classInit.copy(tpe=newInitTpe)} { ..${monadDefn +: body} }",
          decltpe=Some(t"${clsType}[$l, Free[$f, ?]]")
        ).syntax)

        val newInterfaceBody = body.map {
          case Defn.Def(mods, name, tparms, paramss, Some(t"Free[$f, $a]"), defn) => Decl.Def(mods, name, tparms, paramss, t"$f[$a]")
          case other => other
        }
        val monadDecl = q"def MonadF: Monad[F]"

        val newInterface = q"""
        abstract class ${Type.Name(name)}[..$interfaceTargs] {
          ..${monadDecl +: newInterfaceBody}
        }
        """

        val patchInterfaceClass = Patch.replaceTree(target, newInterface.syntax)
        patchInterpDefn + patchInterfaceClass
      }
    }).flatten.asPatch
  }
}
