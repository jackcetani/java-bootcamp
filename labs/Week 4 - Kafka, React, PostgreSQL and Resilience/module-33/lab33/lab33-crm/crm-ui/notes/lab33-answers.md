# Lab 33 Answers

## Reflection Questions

1. **Which design decision most affected correctness?**\
The most crucial design decision was using `customerId` as the React key instead of array index. An index key looks fine until the list is sorted, filtered, or reordered. Once this happens, React silently reuses the wrong DOM node for the wrong customer.
2. **Which failure was hardest to diagnose?**\
Getting `getByLabelText` to actually find each input took me a while. A mismatched `id`/`htmlFor` pair fails silently in the browser and only shows up as a test failure, which makes it easy to miss without running RTL.
3. **What evidence proves the implementation works?**\
The three RTL tests running green TWICE, combined ith the accessibilty tree showing exactly ONE `main` landmark. Together they prove both correctness and the accessibility contract, not just that the page rendered something.
4. **What breaks first at ten times the component count?**\
At ten times the component count, the prop-drilling through `App` would become too fast. Passing `customers`, `draft`, `errors`, and every callback down through more layers of composition is exactly the kind of thing that pushes a real app toward context or a state library well before ten times this component count.
5. **Which concern should move to shared UI infrastructure?**\
Both the `StatusBadge` label and color mapping, as well as the labeled-input/error-alert pattern in `CustomerForm` should be the first things to move. Both are generic enough to become shared pieces rather than being redefined per-feature as the app grows.
6. **What must change before real customer data is used in the UI (spoiler: still fictional here)?**\
Before real customer data is used, real authn, authz, validation, and data-handling / retention policies all need to be implemented. Every fixture and log here only sees the fictional data for a reason.
7. **How does this lab connect to Labs 34–36?**\
Lab 34 lifts these components' data into `useState` CRUD without rewriting their markup. Lab 35 swaps the fixture array for actual `fetch` calls against Spring Boot. Lab 36 will add login/tokens in front of everything.
8. **What metric matters most on the CI dashboard for this gate?**\
Te most important metric for this gate is whether the RTL suite and `npm run build` are both green. A red build or test run should block merge, since neither Lab 34 nor 35 can safely build on top of a component contract that isn't actually proven.
9. **(Forward look) Which props stay stable when Lab 34 lifts state?**\
`CustomerCard`'s `{ customer, onEdit }`, `CustomerList`'s `{ customers, onEdit }`, and `CustomerForm`'s `{ value, errors, onChange, onSubmit, onCancel }` all stay exactly the same. Only where those values come from (fixtures vs. `useState`) will change in lab 34.