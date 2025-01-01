FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle build files
COPY build.gradle.kts gradlew gradlew.bat settings.gradle.kts /app/
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Copy the JAR file (assuming it's built and located in build/libs)
COPY build/libs/financetracker-0.0.1-SNAPSHOT.jar tracker.jar

# Expose the application port
EXPOSE 8080

# Set the entry point for the container
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=default", "tracker.jar"]