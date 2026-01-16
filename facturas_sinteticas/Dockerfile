# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom first to cache dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -ntp -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -ntp -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the fat jar produced by Spring Boot
COPY --from=build /app/target/*.jar /app/app.jar

# Expose default Spring Boot port (change if needed)
EXPOSE 8080

# Tuning defaults (override with --env JAVA_OPTS="...")
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
