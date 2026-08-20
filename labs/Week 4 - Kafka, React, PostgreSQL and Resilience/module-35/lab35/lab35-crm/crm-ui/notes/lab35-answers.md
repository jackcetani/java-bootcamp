# Lab 35 Answers

## Reflection Questions

1. **Which design decision most affected correctness?**\
The most crucial design decision was treating `error` as a fully distinct state from `data`. It's the only thing standing between an API problem vs. no customers in the list, which look identical to a user if you get this wrong.
2. **Which failure was hardest to diagnose?**\
The hardest failure to diagnose was getting all the backend from the past labs consolidated and ready for use in this lab. It took me a while to see that the backend I was using had not `PUT /api/customers/{id}`.
3. **What evidence proves the implementation works?**\
The response-class test suite passing twice and the Network-tab showing the correlation header actually left the browser. Together they prove both the code paths and the real wire behavior, not just a mocked approximation.
4. **What breaks first at ten times the request rate?**\
At ten times the request rate, the lack of request de-duplication or caching beyond the single in-flight `AbortController` would become a problem. Ten times the traffic would mean ten times the redundant `GET /customers` call on every remount, with nothing her to coalesce or cache them.
5. **Which concern should move to shared infrastructure?**\
The CORS configuration should move to shared infrastructure. Right now it's one hardcoded origin in one Spring config class, but a real multi-env deployment would need that sourced from the env-specific config, not a compiled literal.
6. **What must change before real customer data is used?**\
Real authentication in lab 36 would need to be implemented first. Also, HTTPS everywhere, not `localhost`. 
7. **How does this lab connect to Labs 33–34 and Lab 36?**\
Every component prop shape from lab 33 and every piece of CRUD and mode logic from lab 34 are unchanged here. Only the data source moved from in-memory to real `fetch` calls. Lab 35 will do the same and wrap `request()` with auth, without forking it or touching `ApiError`'s shape.
8. **What metric matters most on the CI/ops dashboard for this gate?**\
The response-class test suite staying green is what matters most for this gate. A single regression there is a strong signal that would otherwise only surface as a confusing bug report from a real user closing a tab at the wrong moment.
9. **(Forward look) Where will the bearer token attach without leaking to other origins?**\
The bearer token will attach inside `request()` in `http.ts`. It's the one place every outbound call already funnels through, so attaching `Authorization` there guarantees it only ever goes to `VITE_CRM_API_URL`, never accidentally to some other origin a future fetch call might target.