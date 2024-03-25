# Use a base image with Java installed
FROM openjdk:8-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from your host to the container
COPY target/splitwise-0.0.1-SNAPSHOT.jar /app/splitwise.jar

# Expose the port your Spring Boot application listens on
EXPOSE 8080

# Command to run your Spring Boot application when the container starts
CMD ["java", "-jar", "splitwise.jar"]
