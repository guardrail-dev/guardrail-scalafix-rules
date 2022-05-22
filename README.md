scalafix rules to automate work done in guardrail
===

Available rules
---

| Name                        | Description                                                                   |
| --------------------------- | ----------------------------------------------------------------------------- |
| GuardrailInterpreter2TF     | Given an object definition with an `def apply[A](term: F[A]): G[A] = term match { case Foo(a, b) => ...; ... }` as well as a number of `def foo(a: A, b: B): F[C] = ???` stubs, apply a set of heuristics to migrate the interpreter bodies from `~>` to the associated Tagless Final method stubs.<br />This only rewrites functions that have `???` as the body and that have terms that very closely match the stub method names (at the time of writing, only the first letter of the method is lower-cased. If the method is not found, it does nothing for that term). |
| GuardrailInterface2TF       | Given a `Free[F, A]` with an implicit `InjectK`, presume quite a bit about the structure of the `Free` interpreter and transform them to the equivalent Tagless Final encoding |
| GuardrailScalaResponseTypes | Given some source tree that looks to follow the guardrail-generated `def foo(respond: FooResource.fooResponse.type)(...): FooResource.fooResponse` style, rewrite the `fooResponse` type to be `FooResponse`, reflecting a change in term case normalization in guardrail |
