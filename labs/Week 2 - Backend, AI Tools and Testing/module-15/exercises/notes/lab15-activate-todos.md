## Exercise 5 - Fill Activate Ravi Pseudocode:

### Goal

Complete fill-in blanks for activate(CUS-1002) pseudocode.

customer = repo.findById(`CUS-1002`)
if customer is null → throw `NotFound`
if status is not `PROSPECT` → throw `IllegalState`/`domain exception`
set status to `ACTIVE`
repo.`save/update`(customer)
log correlation `lab-request-001`

Repository saves state; it does not decide PROSPECT→ACTIVE.*