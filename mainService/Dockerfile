# Stage 1: Build with Maven
FROM maven:3.9.9 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run with JRE
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/mainService-*.jar ./app.jar

# Expose port (matches server.port in application.yml)
EXPOSE 8080

# Health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/v1/status || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
