# Lifecycle evidence — Lab 9

Run each phase separately and paste a short excerpt.

| Phase | Command | BUILD SUCCESS? | Notes                                                                                                                                                   |
| ----- | ------- |----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| validate | `mvn validate` | YES            | Success in 0.113s                                                                                                                                       |
| compile | `mvn compile` | YES            | Compiling 9 source files to target                                                                                                                      |
| test | `mvn test` | YES            | Tests run: 1, Failures: 0, Errors: 0, Skipped: 0                                                                                                        |
| package | `mvn package` | YES            | Building jar: ...\lab9\lab9-crm\target\customer-service.jar                                                                                             |
| verify | `mvn verify` | YES            | Tests run: 1, Failures: 0, Errors: 0, Skipped: 0                                                                                                        |
| install | `mvn install` | YES            | Installing ...\lab9\lab9-crm\pom.xml to C:\Users\jackc\.m2\repository\com\northstar\customer-service\0.1.0-SNAPSHOT\customer-service-0.1.0-SNAPSHOT.pom |

Confirmed the JAR (and generated POM) landed under `~/.m2/repository/com/northstar/customer-service/0.1.0-SNAPSHOT/`.

- First `compile` after a fresh clone/copy took noticeably longer — that's Maven Central
  downloading `spring-context` and its transitives into `~/.m2` for the first time.
  Every phase after that was fast (Maven skips re-downloading anything it already has).
- Ran all six phases in order, individually, per the guide — not one `mvn package` shortcut.

## Dependency tree

Save `mvn dependency:tree` output to `docs/dependency-tree.txt` and mark direct vs transitive; confirm junit is test scope.

# Notes
- DIRECT: spring-context (compile scope) and junit-jupiter (test scope)
- TRANSITIVE: Everything indented under spring-context (spring-aop/beans/core/expression)
- junit-jupiter must stay on `test` scope specifically so it never shows up on the compile/runtime classpath of anything that depends on this artifact

Downloaded from central: https://repo.maven.apache.org/maven2/org/codehaus/plexus/plexus-component-annotations/2.0.0/plexus-component-annotations-2.0.0.jar (4.2 kB at 11 kB/s)
 com.northstar:customer-service:jar:0.1.0-SNAPSHOT
 +- org.springframework:spring-context:jar:6.2.3:compile
 |  +- org.springframework:spring-aop:jar:6.2.3:compile
 |  +- org.springframework:spring-beans:jar:6.2.3:compile
 |  +- org.springframework:spring-core:jar:6.2.3:compile
 |  |  \- org.springframework:spring-jcl:jar:6.2.3:compile
 |  +- org.springframework:spring-expression:jar:6.2.3:compile
 |  \- io.micrometer:micrometer-observation:jar:1.14.4:compile
 |     \- io.micrometer:micrometer-commons:jar:1.14.4:compile
 \- org.junit.jupiter:junit-jupiter:jar:5.11.4:test
    +- org.junit.jupiter:junit-jupiter-api:jar:5.11.4:test
    |  +- org.opentest4j:opentest4j:jar:1.3.0:test
    |  +- org.junit.platform:junit-platform-commons:jar:1.11.4:test
    |  \- org.apiguardian:apiguardian-api:jar:1.1.2:test
    +- org.junit.jupiter:junit-jupiter-params:jar:5.11.4:test
    \- org.junit.jupiter:junit-jupiter-engine:jar:5.11.4:test
       \- org.junit.platform:junit-platform-engine:jar:1.11.4:test
 ------------------------------------------------------------------------
 BUILD SUCCESS
 ------------------------------------------------------------------------
 Total time:  4.652 s
 Finished at: 2026-07-30T11:23:09-04:00
 ------------------------------------------------------------------------
