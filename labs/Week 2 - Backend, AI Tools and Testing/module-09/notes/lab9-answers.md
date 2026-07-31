## Lab 9 Answers

### Manual Verification:

| Step                                                                                  | Result |
|---------------------------------------------------------------------------------------|--------|
| `pwd` ends with `lab9-crm`                                                            | PASS   |
| `mvn validate` ... `mvn install` each succeed individually                            | PASS   |
| `mvn test` runs `PlaceholderTest` with 0 failures                                     | PASS   |
| `mvn dependency:tree` shows `spring-context` (compile) and `junit-jupiter` (test)     | PASS   |
| `mvn help:active-profiles` shows `dev` by default; `-Pprod` activates `prod`          | PASS   |
| `java -jar target/customer-service.jar` prints skeleton banner / example customer IDs | PASS   |
| `mvn -B verify` succeeds non-interactively                                            | PASS   |
| Search POM/properties for passwords --> none                                          | PASS   |
| `git status` does not stage `target/` or secrets                                      | PASS   |
| Concepts/reflection drafts mention artifact GAV vs `CUS-1001` distinction             | PASS   |

### Concepts to Discuss:
1. **The main data or request flow in this lab (source → compile → package → optional install)**\
source code → `compile` turns into `.class` files → `package` bundles into a JAR → optional `install` puts it in the local Maven repo for other projects to use.
2. **The trust boundary between Maven Central artifacts and your own source**\
Maven Central artifacts are technically trusted enough to build with, but are ultimately untrusted inputs. Our own source code is trusted. CI and security reviews enforce validation, authn/authz, and secrets management to maintain this boundary.
3. **The success and failure contract of each lifecycle phase**\
Every lifecycle phase either succeeds and moves to the next phase, or it fails and the build stops. There are no partial-success states in this program.
4. **Stable identity of the artifact (groupId:artifactId:version) versus customer IDs (CUS-1001)**\
The artifact identity `com.northstar:customer-service:0.1.0-SNAPSHOT` is stable and identifies this codebase. `CUS-1001` is a sample customer ID that identifies the customer inside it. They are totally different but easy to confuse.
5. **Retry and idempotency of mvn install (safe to repeat; overwrites snapshot)**\
It is completely safe to run `mvn install` multiple times. It simply overwrites the snapshot in the local Maven repository. This is idempotent and does not affect the source code or the artifact identity.
6. **Local development shortcut (dev profile) versus production design (prod)**\
`dev` profile is a local development convenience that is used to simply get it running. `prod` profile is designed for production and should never be used with real secrets in a development environment.
7. **Logs or evidence needed when a CI build fails**\
When a CI build fails, the full console logs and whatever `target/surefire-reports/` says if a test failed.
8. **Behavior with two application instances built from the same POM version**\
They will have the same functionality and behavior, but may have different runtime configurations or dependencies. No shared state unless something outside Maven connects them.
9. **Why test scope keeps JUnit out of the runtime image mindset**\
If it leaked onto `compile` or `runtime`, it would be a production dependency. Anyone pulling in `customer-service.jar` as a dependecy would drag a whole test framework along with it. Test scope ensures that JUnit is only used for testing and not included in the final artifact.
10. **Why CI prefers verify over casually installing snapshots on shared agents**\
`install` writes to the local Maven repository, which on shared CI agents can lead to conflicts or pollution of the repository. `verify` runs all tests and checks without installing, ensuring a clean build environment.

### Security and Production Review
1. **Which inputs are untrusted? (Downloaded Maven artifacts; later API inputs)**\
Untrusted inputs include Maven artifacts and whatever future API inputs will be received once the API is implemented.
2. **Where are authn/authz/validation enforced later? (App layers + CI/repo managers)**\
They will be enforced in the application layers (controllers, services) and in CI/repo managers to ensure that only authorized and validated inputs are processed.
3. **Which values are sensitive, and where stored? (Never in POM; use secrets stores)**\
Sensitive values should never be stored in the POM or source code. Profile properties are for non-secret config only. Secrets should be stored in secure secret stores or environment variables.
4. **What can be retried safely? (mvn verify, snapshot install)**\
`mvn verify` and snapshot installs can be retried safely as they are idempotent operations that do not affect the source code or artifact identity.
5. **What happens after a partial failure? (Failed test stops verify; no bad promotion in CI)**\
A failed test will stop the `verify` phase and the build will not proceed to the next phase until the failure is resolved. There are no partial failures with this phase ordering.
6. **What would an operator monitor? (CI duration, failed verify jobs)**\
An operator would monitor CI build duration and failed-verify-job counts, once there's an actual CI pipeline running this.
7. **Which local default is unacceptable in production? (dev profile active by default with real secrets—never do that)**\
The `dev` profile being active by default is fine for training, but in production, it is unacceptable to have real secrets in the `dev` profile. Production should always use the `prod` profile with proper secret management.
8. **How are contracts versioned? (Artifact version + later OpenAPI/WSDL)**\
Contracts are versioned using artifact versions. Later, OpenAPI or WSDL definitions can be used to define and manage API contracts.

### Reflection Questions
1. **Which design decision most affected build correctness?**\
Splitting Spring into `compile` scope and JUnit into `test` scope was the most impactful decision. It ensured that the build was correct and that the final artifact did not include unnecessary dependencies. If this design decision were changed,the whole build would fall apart.
2. **Which failure was hardest to diagnose?**\
The dependency-tree anotation step was hardest to diagnose because it doesn't obviously fail. Somebody would have to read the indentation to tell direct from transitive.
3. **What evidence proves the lifecycle walk was real (not only package once)?**\
`docs/lifecycle-evidence.md` shows the output of each of the 6 lifecycle phase, proving that the build went through all phases and not just packaged once.
4. **What breaks first at ten times the dependency count?**\
The dependency tree becomes hard to read manually, and version conflicts between transitive dependencies start showing up more often. This is where `dependencyManagement` and `exclusions` become more important to manage.
5. **Which concern should move to shared infrastructure (artifact repository, CI cache)?**\
Artifact repository and CI cache should be used to manage dependencies and artifacts, rather than relying on local builds. This ensures that all developers and CI agents are using the same versions of dependencies and artifacts, reducing the risk of conflicts and inconsistencies.
6. **What must change before real customer data is used?**\
Nothing in this build lab touches real data yet, but eventually, the application will need to implement proper authentication, authorization, and validation to ensure that only authorized users can access and modify customer data. Additionally, sensitive data should be encrypted and stored securely. Also, the `prod` profile needs hardening, not just a different label.
7. **How does this lab connect to Lab 8 structure and Lab 10+ code?**\
The package structure from Lab 8 remains unchanged. Lab 9 simply wraps it in a build that can actually produce a shareable JAR. Lab 10 will start filling in real domain code on top of this exact build.
8. **What metric, log field, or CI signal matters most when verify fails?**\
Which tests failed, the number of failed tests, and why is what matters most. The Surefire report and console logs will provide this information. Additionally, the CI signal of a failed build is important to catch and address issues quickly.
9. **Why is test scope on JUnit more than a style preference?**\
Test scope in JUnit is more than a style preference because it determines the visibility and accessibility of test methods and classes, not just a cosmetic choice. It ensures that tests are properly isolated and can be executed independently.
10. **(Forward look) When Spring Boot arrives, what stays stable in this POM vs what changes first?**\
The <dependencies> section will change first to include Spring Boot dependencies, but the overall structure of the POM and the build lifecycle will remain stable. The artifact coordinates and packaging will also remain the same, ensuring that the build process is consistent even as new dependencies are added. The profiles will likely get real environment-specific values instead of just a label.