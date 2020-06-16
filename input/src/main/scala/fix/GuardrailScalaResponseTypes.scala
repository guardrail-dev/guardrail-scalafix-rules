/*
rule = GuardrailScalaResponseTypes
*/
package fix
import deps.GuardrailScalaResponseTypes.{FooHandler, FooResource}
import scala.concurrent.Future

object GuardrailScalaResponseTypes {
  class FooHandlerImpl extends FooHandler {
    override def doFoo(respond: FooResource.doFooResponse.type)(): Future[FooResource.doFooResponse] = Future.successful(respond.Ok)
  }
}
