## #Exercise 2 — Stub vs Verify

### Goal

Explain stubbing return values versus verifying calls for activate.

`when(repo.findById("CUS-1002))`  // STUB: arrange an input
    `.thenReturn(raviProspect);`
`verify(repo).save(activatedRavi);`  // VERIFY: assert a side effect

// Stubs feed inputs; verifiers prove side effect calls happened.