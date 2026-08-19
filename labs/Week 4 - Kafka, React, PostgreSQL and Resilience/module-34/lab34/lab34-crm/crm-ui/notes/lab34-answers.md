# Lab 34 Answers

## Reflection Questions

1. **Which design decision most affected correctness?**\
The most crucial design decision was using a discriminated `Mode` union instead of separate `isCreating` or `isEditing` booleans. It makes it so create and edit can't be open at the same time, a state TypeScript won't let exist.
2. **Which failure was hardest to diagnose?**\
Getting per-field error clearing right was the hardest. `CustomerForm` reports the whole updated draft object on every keystroke, not which field changed, so figuring out you should only clear the error for the field that actually changed wasn't obvious.
3. **What evidence proves the implementation works?**\
The 8 flow tests covering create/edit/cancel/search/validation passing twice in a row, and the React DevTools panel showing the actual state shape. Together they prove both the behavior and state itself is behaving as expected, not just the UI looks right.
4. **What breaks first at ten times the list size?**\
At ten times the list size, the linear `.filter()` running on every keystroke would start to lag. It's not a UI fix, but a correctness fix.
5. **Which concern should move to shared infrastructure?**\
The mode union and draft/errors patterns should move first. They are generic enough to become a reusable hook once more than one page needs the same create/edit/cancel shape.
6. **What must change before real customer data is used?**\
Real persistence from Lab 35's API, real auth from Lab 36, and server-side validation all need to exist before real customer data is used. This lab's data disappears on every refresh (by design), which is unacceptable for real customer data.
7. **How does this lab connect to Labs 33 and 35?**\
Lab 33's component props (`CustomerCard`, `CustomerList`, `CustomerForm`) are reused here unchanged except for the `saving` and search wiring additions. Lab 35 will do the same, swapping `useState` arrays for `fetch` calls without touching this lab's `Mode`/draft shapes.
8. **What metric matters most on the CI dashboard for this gate?**\
Whether test suite and build are both green is what matter most. A single failing flow test is a strong signal that a state-management regression slipped in, which is exactly what this gate intends to catch before Lab 35 builds on top of it.
9. **(Forward look) Which state will become request-status enums in Lab 35?**\
The `saving` boolean is the best candidate. It'll likely become a real request-status union once Save actually waits on a network response instead of completing synchronously.