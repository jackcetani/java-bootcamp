# Lab 19 — Regression Notes

- **Chrome / Chromium version:** 150.0.7871.187
- **API IT evidence** (CUS-1001 get + create correlation): see 'api-it1.png' and 'api-it2.png' in 'lab19-crm/notes/screenshots/'
- **UI IT evidence** (screenshot path under notes/screenshots/lab-19/): see 'ui-it1.png' and 'ui-it2.png' in 'lab19-crm/notes/screenshots/'
- **Negative case:** blank name / 404 / deliberate bad locator screenshot: see 'ui-failure1.png' and 'ui-failure2.png' in 'lab19-crm/notes/screenshots/'

## Concepts to Discuss
1. **Main request flow: UI/API → controller → service → repository**\
   Browser / API -> `CustomerController` -> `CustomerService` -> `CustomerRepository`. Same layering direction as every lab before, just with HTTP on top now.
2. **Trust boundary: validation at API/UI edge vs service rules**\
   The controller / service edge is where blank name and IDs get rejected with a 400. The UI only display whatever the API says and has none of its own validation logic. The real rule lives in only one place deliberately.
3. **Success/failure contracts (201/200/400/404, visible result text)**\
   201 + `X-Correlation-Id` header -> `create`; 200 -> GET; 404 -> unknown ID; 400 -> visible error message on blank input. The UI's result region will show enough text to tell which of these success/failures took place.
4. **Stable identities (CUS-1001) vs random data in IT**\
   CUS-1001/1002 are reserved only for GET checks since they're pre-seeded. Any test that actually calls `create` should use a different, distinct ID so it doesn't silently overwrite the seeded fixtures our other tests depend on.
5. **Idempotency of repeated create and UI double-submit**\
   GET is naturally idempotent and can be safely retried. POST is not idempotent, since this lab has no duplicate-ID check unlike previous labs. A repeated `create` for teh same ID will silently overwrite rather than reject.
6. **Local headed Chrome vs CI headless WebDriverManager**\
   Locally, running headed Crhome is useful for watching a test actually click through the form while debugging. CI always runs headless since there's no display, and the exact same Page Object code works in both modes.
7. **Evidence: surefire, screenshots, correlation header echo**\
   The surefire output proves what ran and what passed. Screenshots will prove what a failure (or pass) actually looks like. The `X-Correlation-Id` echo in HTTP responses can be used to tie any of these back to a traceable request.
8. **Two instances: port conflicts, shared DB/map contamination**\
   Two app instances would mean two in-memory stores with no shared state. However, Spring Boot can cache and share one application context with the same config, meaning there is a risk that `CustomerApiIT` and `CustomerUiIT` may share the same `InMemoryCustomerRepository`, which is why fixture ID choices matter.
9. **Why Page Objects reduce brittle locator duplication**\
   Every locator lives in exactly one class. If a `data-testid` ever gets renamed, there's one file to fix instead of hunting through every test method that happened to reference it, making things more modular.
10. **What Lab 20 will add (structured logs) without changing fixture IDs**\
    Lab 20 will add structured logging keyed off the same `lab-request-001` corr ID and the same CUS-1001/1002 fixtures. Nothing about the IDs or the create/get contracts gets written, logging just gets layered on top.
