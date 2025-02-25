# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Install curl and bash
RUN apt-get update && apt-get install -y curl bash && rm -rf /var/lib/apt/lists/*

# Set the working directory in the container
WORKDIR /app

# Copy the packaged jar file into the container (ensure the target/*.jar path matches)
COPY target/*.jar /app/app.jar

# Download the wait-for-it.sh script directly from the URL and make it executable
RUN curl -sSL https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh -o /app/wait-for-it.sh \
    && chmod +x /app/wait-for-it.sh

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the wait-for-it.sh script to wait for PostgreSQL to be ready, then run the jar file
ENTRYPOINT ["/bin/bash", "/app/wait-for-it.sh", "postgres:5432", "--", "java", "-jar", "/app/app.jar"]
