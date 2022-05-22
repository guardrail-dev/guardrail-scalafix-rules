package fix
import deps.GuardrailIterableToVector.{FooHandler, FooResource}
import scala.concurrent.Future

object GuardrailIterableToVector {
  class FooHandlerImpl extends FooHandler {
    override def doFoo(respond: FooResource.DoFooResponse.type)(xs: Vector[Vector[Vector[String]]], y: Option[String]): Future[FooResource.DoFooResponse] = {
      Future.successful(respond.Ok)
    }
  }
}
