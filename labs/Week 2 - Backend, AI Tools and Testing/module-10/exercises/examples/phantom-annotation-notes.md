## Exercise-03-phantom-annotation-notes:

### Goal

Flag Copilot-style annotations that do not belong in a plain Java prep sketch.

| Seen in suggestion | Likely real? | Prep action |
| --- | --- | --- |
| @Entity / @Table | JPA only | Defer — not Lab 10 scope |
| @Service / @Autowired | Spring | Defer — hosting labs later |
| @NotNull (Jakarta) | Validation lib | Name it; don't invent imports |
| public record Customer(...) | Java 16+ | OK on JDK 21 |