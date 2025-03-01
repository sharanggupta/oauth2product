# OAuth2 Product

## Prerequisites
- Docker and Docker Compose
- JDK 17
- Maven
- jq (for JSON processing)

## Steps to run

1. Build the application (including executing tests):
```bash
./mvnw clean package
```

2. Start the services:
```bash
docker compose up -d
```

3. Wait for all the services to be healthy (this may take a few minutes):
```bash
docker compose ps
```
4. Try accessing the products endpoint without authentication (should return 401 Unauthorized):
```bash
curl -v -X GET http://localhost:8080/products
```

5. Get an access token:
```bash
access_token=$(curl -X POST http://localhost:8081/realms/product-realm/protocol/openid-connect/token \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "grant_type=password" \
-d "client_id=product-client" \
-d "client_secret=product-secret" \
-d "username=test-user" \
-d "password=password" | jq -r .access_token)
```

6. Access the products endpoint:
```bash
curl -X GET http://localhost:8080/products \
-H "Authorization: Bearer $access_token"
```

## Service URLs
- Keycloak: http://localhost:8081
- Resource Server (Product API): http://localhost:8080
- OAuth2 Client: http://localhost:8082

## Cleanup
```bash
docker-compose down -v
```