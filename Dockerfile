# Stage 1 — build
# Downloads dependencies and compiles into a fat JAR.
# This layer is cached: if pom.xml hasn't changed, Maven skips the download step.
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# Stage 2 — runtime
# Only the JRE + compiled JAR. No Maven, no JDK, no source code in the final image.
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/membership-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
