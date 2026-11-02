# Multi-stage Dockerfile for Student Placement Management System
# Stage 1: Build stage using Maven and Eclipse Temurin JDK 21
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy POM and download dependencies first for efficient caching
COPY pom.xml .
COPY src ./src

# Build the Spring Boot executable JAR without running tests inside the image build
RUN mvn -B clean package -DskipTests

# Stage 2: Minimal runtime environment using Eclipse Temurin JRE 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run container as a secure, unprivileged non-root user
RUN useradd --system --uid 10001 appuser
COPY --from=build /workspace/target/student-placement-0.0.1-SNAPSHOT.jar app.jar
USER 10001

EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
