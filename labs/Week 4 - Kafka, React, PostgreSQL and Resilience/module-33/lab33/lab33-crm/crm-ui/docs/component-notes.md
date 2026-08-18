# Lab 33 — Component notes

## List keys

`customerId` is the React key because it's the one identifier guaranteed to stay attached to the same logical customer regardless of sort order, filtering, or array position. An index key would cause React to reuse the wrong DOM node (and wrong focus state) after a sort or filter changes array order.
## A11y

`StatusBadge` always renders visible label text (`Active`, `Suspended`, etc.), not just a colored dot, so status is understandable in grayscale or to a screen reader. `CustomerForm` pairs every input with a real `<label htmlFor>`/`id`, and field errors live in a `role="alert"` region tied via `aria-describedby`, so assistive tech announces errors the same way sighted users see them.

## Concepts to Discuss

1. **Main UI flow (fixtures → list → card → edit callback)**\
   `APP` holds the fixture array and passes it to `CustomerList`, which then renders a `CustomerCard` for every customer. Each card's `Edit` button calls back up to `App` with just the `customerId`, keeping the cards state-free.
2. **Trust boundary: browser DOM is untrusted display; validation moves up in Lab 34–35**\
   Nothing in this lab validates input yet, the form only reports draft changes upward via props. Real validation moves into `App`/state in lab 34 and eventually the API in lab 35.
3. **Success/failure contracts: empty vs populated list; build/typecheck must pass**\
   An empty customer array must render `EmptyState` instead of a blank or broken grid. `npm run build` passing is the other hard contract, as a red build blocks any merge regardless of how the UI looks.
4. **Stable identity: `customerId` as React `key` and edit callback argument**\
   `customerId` is used as the React `key` on `CustomerCard` as well as the argument passed to `onEdit`. One stable identifier serves both React and the app's business logic, so there's no risk of the two disagreeing.
5. **Idempotency of `npm test` (repeatable, no shared mutable DOM fixtures)**\
   Each `npm test` calls `render()` fresh and reads from `seedCustomers` without mutating it. Thus, running the suite multiple times will produce the same result.
6. **Why presentational components before `useState` (Lab 34) and fetch (Lab 35)**\
   Building `CustomerCard`, `CustomerList`, and `CustomerForm` as pure, prop-driven components means lab 34 can lift state up to `App` and lab 35 can swap fixtures for `fetch` without touching this lab's markup at all. Only the data source changes, not the shape consuming it.
7. **Evidence operators/leads need (RTL green + screenshot of a11y tree landmarks)**\
   A green RTL suite proves behavior, but the accessibility tree showing heading landmarks, a single `main`, and labeled form fields is what really proves the a11y claims. Text evidence saying it works isn't enough for this gate.
8. **Two students: same fixtures, same component prop shapes, same test selectors**\
   Since `seedCustomers` and the component prop shapes are frozen in this lab, two different students' RTL tests should query the exacts same roles/names and get identical results.
9. **False confidence: testing class names vs roles and accessible names**\
   A test asserting `.card` exists n the DOM would still pass even if the card had no accessible name, button, or broken labels. It doesn't prove a real user can actually use the page. Role and name queries fail when accessibility breaks.
10. **What Lab 34 will change (lift state) without rewriting card markup**\
    Lab 34 will lift the ficture array into `useState` and wire `CustomerForm`'s `errors` prop to a real validation output. It can do this without rewriting `CustomerCard`, `CustomerList`, or `CustomerForm`'s markup, since their props contracts are already frozen in this lab.


## Lab 34 handoff

`CustomerForm`, `CustomerCard`, and `CustomerList` keep their exact prop shapes into Lab 34 — only `App` changes, lifting `customers`/`draft` into real `useState` CRUD and wiring `errors` to `validateCustomer(draft)` output. No card or form markup should need to change.