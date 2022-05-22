/*
rule = GuardrailIteratorToVector
*/
package fix
import deps.GuardrailIteratorToVector.{FooHandler, FooResource}
import scala.concurrent.Future

object GuardrailIteratorToVector {
  class FooHandlerImpl extends FooHandler {
    override def doFoo(respond: FooResource.DoFooResponse.type)(xs: Iterator[Iterator[Iterator[String]]]): Future[FooResource.DoFooResponse] = {
      Future.successful(respond.Ok)
    }
  }
}
