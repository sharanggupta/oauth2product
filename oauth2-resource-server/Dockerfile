FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl bash && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/*.jar /app/app.jar

RUN curl -sSL https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh -o /app/wait-for-it.sh \
    && chmod +x /app/wait-for-it.sh

EXPOSE 8080

ENTRYPOINT ["/bin/bash", "/app/wait-for-it.sh", "postgres:5432", "--", "java", "-jar", "/app/app.jar"]