# Steps to run
1. ./mvnw clean package
2. docker-compose up
3. Go to http://localhost:8081/ keycloak and create a new product-realm by importing the file keycloak-realm.json
4. access_token=$(curl -X POST http://localhost:8081/realms/product-realm/protocol/openid-connect/token \
   -H "Content-Type: application/x-www-form-urlencoded" \
   -d "grant_type=password" \
   -d "client_id=product-client" \
   -d "client_secret=product-secret" \
   -d "username=test-user" \
   -d "password=password" | jq -r .access_token)
5. curl -X GET http://localhost:8080/products \
   -H "Authorization: Bearer $access_token"