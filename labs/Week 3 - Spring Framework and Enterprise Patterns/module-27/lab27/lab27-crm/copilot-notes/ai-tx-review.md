# AI TX Review — Lab 27 (lab27-001)

**Date**: 8/12/26\
**Status:** **MANUAL**\
**Manual Review Notes**:\
No live Copilot session used for this submission. Reviewed the implementation by hand against the anti-patterns mentioned in the guide.
- Ensured `@Transactional` only lives on `TransferService.transfer()`. The annotation does not appear anywhere in `TransferController.java`
- Ensured there is no try/catch in `TransferService.transfer()`. `InsufficientFundsException`, `AccountNotFoundException`, and `IllegalStateException` all propagate unchecked, making sure no exception is caught and swallowed before reaching the transaction proxy.
- Ensured `transfer()` is called externally through the injected `TransferService`, never through `this`. Springs AOP proxy is always in the call path.\

**Decision**: Accepted. Reviewed manually against the guide's outlined pitfalls and ensured correctness
