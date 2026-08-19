# Lab 34 — State notes

## Lifted state

**Why create/edit mode lives in App, not in CustomerCard.**\
Create and edit mode lives in `App` and not `CustomerCard` because only one form can be open at a time across the whole page. If each card owned its own edit flag, nothing would stop two cards from entering edit mode at the same time, and the toolbar's `Add customer` button would have no single place to coordinate with. Lifting `mode`/`draft`/`errors` into `App` is what makes the `Mode` union's exclusive and meaningful.

## Validation

**Client validation is UX only; server re-validates in Lab 35.**\
`validateCustomer` only protects the in-memory array from bad data and gives the user instant feedback. However, it is not a security boundary and nothing here is authoritative. Lab 35's real API will re-validate every field server-side regardless of what this client-side check allows through, exactly the same way a client-side form check never replaces server validation in a real system.

## Concepts to Discuss

1. **Main state flow (events → setState → derived render)**\
   A user event like typing or clicking a button calls a setter. React then re-renders `App` with the new state and everything downstream is recalculated fresh from that state on every render. Nothing is cached outside of `useState`.
2. **Trust boundary: client validation is UX; server will re-validate (Lab 35)**\
   `validateCustomer` exists to give the user immediate feedback and stop obviously bad data from entering the in-memory array. It is not a security boundary.
3. **Success/failure contracts: invalid submit shows field errors; cancel discards draft**\
   An invalid submit sets `errors` and returns early, `customers` never changes. Cancel always resets `draft` and `errors`, and closes the form without touching `customers` at all, regardless of what was typed.
4. **Stable identity: `customerId` for edit mode and list keys**\
   `customerId` is what `openEdit` uses to find the right row, what `.map` matches on, and what `CustomerList` still uses as its React key. It's the one identifier serving all three purposes, keeping them from disagreeing.
5. **Retry / double-submit: disable Save while “saving” flag (soft) before API**\
   The `saving` flag disables the `Save` button for the duration of a submit. It is a soft guard that has no visible effect yet since there's no real latency, but it's the seam Lab 35's async save will plug into.
6. **Why derived `visible` must not be a second `useState`**\
   If `visible` were its own state, every keystroke would trigger an extra render cycle and risk the "stale filtered list" bug where the effect hasn't caught up yet. Deriving it directly during render means it's always exactly in sync with no extra renders.
7. **Evidence: RTL flows + DevTools state screenshot**\
   A green test-suite proves the click-through behavior works, but a React DevTools panel shwoing `customer.length`, `query`, and `mode` at the specific moment is what proves the state shape itself is correct for this lab.
8. **Two browsers: independent memory; no shared server yet**\
   Since everything lives in one component's `useState`, two browser tabs have completely independent copies of the customer list. Nothing is shared until Lab 35 introduces a real server as the single source of truth.
9. **False confidence: mutating arrays in place “works” until Strict Mode**\
   Calling `customers.push()` instead of spreading into a new array can look like it works in a manual check, but it breaks silently and unpredictably under Strict Mode's double-invoke behavior. This is why failure experiment 1 exists.
10. **What Lab 35 changes (fetch) without rewriting mode union shapes**\
    Lab 35 will replace the `useState<Customer[]>(seedCustomers)` array and the synchronous create/update handlers with `fetch` calls and real request states. The `Mode` union, `CustomerDraft` shape, and validation function all stay exactly the same as they are in this lab.
