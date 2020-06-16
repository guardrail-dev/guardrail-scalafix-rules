package deps

object GuardrailScalaResponseTypes {
  trait FooHandler {
    def doFoo(respond: FooResource.doFooResponse.type)(): scala.concurrent.Future[FooResource.doFooResponse]
  }

  object FooResource {
    sealed abstract class doFooResponse(val statusCode: Int)
    object doFooResponse {
      def Ok: doFooResponse = new doFooResponse(200) {}
    }
  }
}
