# Use a multi-stage build
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle.kts gradlew gradlew.bat settings.gradle.kts /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Build the application
RUN ./gradlew build -x test

# Clean up unnecessary files (optional)
RUN rm -rf /app/gradle /app/src

# Use the same base image for the final image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Create the logs directory
RUN mkdir -p logs

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar tracker.jar

# Expose the application port
EXPOSE 8080

# Set environment variables for database connection
ENV DB_HOST=host.docker.internal
ENV DB_PORT=5432
ENV DB_NAME=geetpithadia
ENV DB_USER=geetpithadia
ENV DB_PASSWORD=admin

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=default", "tracker.jar"]
