# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline || true
COPY src ./src
RUN mvn -B -DskipTests package && cp target/*-SNAPSHOT.jar target/app.jar

FROM eclipse-temurin:21-jre
RUN groupadd --system --gid 10001 spring \
 && useradd --system --uid 10001 --gid spring --create-home spring
WORKDIR /app
COPY --from=build --chown=spring:spring /workspace/target/app.jar /app/app.jar
USER 10001
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD ["bash", "-c", "exec 3<>/dev/tcp/127.0.0.1/8080 && printf 'GET /actuator/health/readiness HTTP/1.0\r\nHost: localhost\r\n\r\n' >&3 && cat <&3 | grep -q UP"]
ENTRYPOINT ["java","-jar","/app/app.jar"]