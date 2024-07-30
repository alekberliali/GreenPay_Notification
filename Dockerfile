# Use the official Gradle image to build the application
FROM gradle:7.5.1-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle build files and the source code into the container
COPY build.gradle ./
COPY settings.gradle ./
COPY ./src ./src

# Build the application
RUN gradle build -x test

# Use the official OpenJDK 17 image to run the application
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/build/libs/*.jar /app/myapp.jar

# Expose the port on which the application will run
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
