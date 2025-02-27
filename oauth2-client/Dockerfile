FROM openjdk:17-jdk-slim

RUN apt-get update && apt-get install -y curl bash && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY target/*.jar /app/app.jar

RUN curl -sSL https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh -o /app/wait-for-it.sh \
    && chmod +x /app/wait-for-it.sh

EXPOSE 8082

ENTRYPOINT ["/bin/bash", "/app/wait-for-it.sh", "keycloak:8080", "-t", "120", "--", "java", "-jar", "/app/app.jar"]