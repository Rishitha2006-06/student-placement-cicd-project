# Production Dockerfile for Student Placement Management System
# Base Image: Eclipse Temurin JRE 21 (pre-cached locally)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Run container as a secure, unprivileged non-root user
RUN useradd --system --uid 10001 appuser

# Copy the Spring Boot executable JAR built during Maven package stage
COPY target/student-placement-0.0.1-SNAPSHOT.jar app.jar

USER 10001
EXPOSE 8000

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
