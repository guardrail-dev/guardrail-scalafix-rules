package fix
import deps.GuardrailScalaResponseTypes.{FooHandler, FooResource}
import scala.concurrent.Future

object GuardrailScalaResponseTypes {
  class FooHandlerImpl extends FooHandler {
    override def doFoo(respond: FooResource.DoFooResponse.type)(): Future[FooResource.DoFooResponse] = {
      Future.successful(respond.Ok)
    }
  }
}
