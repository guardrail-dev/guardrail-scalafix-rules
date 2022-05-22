package deps

object GuardrailIteratorToVector {
  trait FooHandler {
    def doFoo(respond: FooResource.DoFooResponse.type)(xs: Iterator[Iterator[Iterator[String]]]): scala.concurrent.Future[FooResource.DoFooResponse]
  }

  object FooResource {
    sealed abstract class DoFooResponse(val statusCode: Int)
    object DoFooResponse {
      def Ok: DoFooResponse = new DoFooResponse(200) {}
    }
  }
}
