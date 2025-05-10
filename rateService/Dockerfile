# Stage 1: Build with Maven
FROM maven:3.9.9 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/rateService-*.jar ./app.jar

# Different port than main-service
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8081/api/v1/status || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
