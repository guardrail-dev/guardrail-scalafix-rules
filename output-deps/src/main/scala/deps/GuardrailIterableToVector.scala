package deps

object GuardrailIterableToVector {
  trait FooHandler {
    def doFoo(respond: FooResource.DoFooResponse.type)(xs: Vector[Vector[Vector[String]]], y: Option[String]): scala.concurrent.Future[FooResource.DoFooResponse]
  }

  object FooResource {
    sealed abstract class DoFooResponse(val statusCode: Int)
    object DoFooResponse {
      def Ok: DoFooResponse = new DoFooResponse(200) {}
    }
  }
}
