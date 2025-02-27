package dev.sharanggupta.oauth2resourceserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;

    private String getAccessToken() throws Exception {
        com.mashape.unirest.http.HttpResponse<String> response = Unirest.post(keycloak.getAuthServerUrl() + "/realms/product-realm/protocol/openid-connect/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .field("grant_type", "password")
                .field("client_id", "product-client")
                .field("client_secret", "product-secret")
                .field("username", "test-user")
                .field("password", "password")
                .asString();

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    @Test
    void testGetAllProducts_Authorized_ShouldReturnAllProducts() throws Exception {
        mockMvc.perform(get("/products").header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[1].name").value("Phone"))
                .andExpect(jsonPath("$[1].price").value(699.99));
    }

    @Test
    void testGetAllProducts_Unauthorized_ShouldReturnUnauthorised() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateProduct_ShouldReturnCreatedProduct() throws Exception {
        String newProductJson = "{ \"name\": \"Tablet\", \"price\": 499.99 }";

        mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(newProductJson)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tablet"))
                .andExpect(jsonPath("$.price").value(499.99));
    }

    @Test
    void testUpdateProduct_ShouldReturnUpdatedProduct() throws Exception {
        // Create a new product
        String newProductJson = "{ \"name\": \"Tablet\", \"price\": 499.99 }";
        String createdProductResponse = mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(newProductJson)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract the created product's ID
        JsonNode createdProduct = new ObjectMapper().readTree(createdProductResponse);
        long productId = createdProduct.get("id").asLong();

        // Update the created product
        String updatedProductJson = "{ \"name\": \"Updated Tablet\", \"price\": 599.99 }";
        mockMvc.perform(put("/products/" + productId)
                        .contentType("application/json")
                        .content(updatedProductJson)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Tablet"))
                .andExpect(jsonPath("$.price").value(599.99));
    }

    @Test
    void testUpdateProduct_NotExisting_ShouldReturnNotFound() throws Exception {
        long notExistingProductId = 100L;
        // Update a non-existing product
        String updatedProductJson = "{ \"name\": \"Updated Tablet\", \"price\": 599.99 }";
        mockMvc.perform(put("/products/" + notExistingProductId)
                        .contentType("application/json")
                        .content(updatedProductJson)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProductById_ShouldReturnProduct() throws Exception {
        // Use the product ID from the data setup by flyway
        long productId = 1L;

        // Get the product by ID
        mockMvc.perform(get("/products/" + productId)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))  // Assuming the product name is "Laptop"
                .andExpect(jsonPath("$.price").value(999.99));  // Assuming the product price is 999.99
    }

    @Test
    void testGetProductById_NotExisting_ShouldReturnNotFound() throws Exception {
        // Use a non-existing product ID
        long nonExistingProductId = 999L;

        // Try to get the non-existing product by ID
        mockMvc.perform(get("/products/" + nonExistingProductId)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductById_ShouldDeleteProduct() throws Exception {
        // Create a new product
        String newProductJson = "{ \"name\": \"Tablet\", \"price\": 499.99 }";
        String createdProductResponse = mockMvc.perform(post("/products")
                        .contentType("application/json")
                        .content(newProductJson)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract the created product's ID
        JsonNode createdProduct = new ObjectMapper().readTree(createdProductResponse);
        long productId = createdProduct.get("id").asLong();

        // Delete the product by ID
        mockMvc.perform(delete("/products/" + productId)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isNoContent());

        // Try to get the deleted product by ID
        mockMvc.perform(get("/products/" + productId)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteProductById_NotExisting_ShouldReturnNotFound() throws Exception {
        // Use a non-existing product ID
        long nonExistingProductId = 999L;

        // Try to delete the non-existing product by ID
        mockMvc.perform(delete("/products/" + nonExistingProductId)
                        .header("Authorization", "Bearer " + getAccessToken()))
                .andExpect(status().isNotFound());
    }
}