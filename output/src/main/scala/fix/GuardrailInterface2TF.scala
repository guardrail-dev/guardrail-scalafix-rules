package fix

object GuardrailInterface2TF {
  abstract class InjectK[F[_], G[_]] {
    def inj[A](fa: F[A]): G[A]
  }

  class Monad[F[_]]
  class Free[F[_], A]
  object Free {
    class InjectPartiallyApplied[F[_], G[_]](private val bogus: String = null) {
      def apply[A](value: F[A])(implicit I: InjectK[F, G]): Free[G, A] = ???
    }
    def inject[F[_], G[_]] = new InjectPartiallyApplied[F, G]()
    def catsFreeMonadForFree[F[_]]: Monad[Free[F, ?]] = ???
  }

  sealed trait ScalaTerm[L, A]

  case class Bar(a: Boolean, b: String)
  case class VendorPrefixes[L](a: Long, b: Bar) extends ScalaTerm[L, List[String]]

  abstract class ScalaTerms[L, F[_]] {
  def MonadF: Monad[F]
  def vendorPrefixes(a: Long, b: Bar): F[List[String]]
}
  object ScalaTerms {
    implicit def scalaTerms[L, F[_]](implicit I: InjectK[ScalaTerm[L, ?], F]): ScalaTerms[L, Free[F, ?]] = new ScalaTerms[L, Free[F, ?]] {
  def MonadF = Free.catsFreeMonadForFree
  def vendorPrefixes(a: Long, b: Bar): Free[F, List[String]] = Free.inject[ScalaTerm[L, ?], F](VendorPrefixes(a, b))
}
  }
}
