package dev.sharanggupta.oauth2client.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import dev.sharanggupta.oauth2client.OAuth2ClientApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = OAuth2ClientApplication.class)
@AutoConfigureMockMvc
class ProductControllerTest {

    private static WireMockServer authServer;
    private static WireMockServer resourceServer;

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    static void setupServers() {
        authServer = new WireMockServer(wireMockConfig().port(8081));
        authServer.start();

        resourceServer = new WireMockServer(wireMockConfig().port(8082));
        resourceServer.start();

        setupStubs();
    }

    @AfterEach
    void resetStubs() {
        authServer.resetAll();
        resourceServer.resetAll();
        setupStubs();
    }

    private static void setupStubs() {
        // Mock OIDC Discovery Endpoint
        authServer.stubFor(WireMock.get(urlEqualTo("/realms/product-realm/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                    {
                        "issuer": "http://localhost:8081/realms/product-realm",
                        "authorization_endpoint": "http://localhost:8081/realms/product-realm/protocol/openid-connect/auth",
                        "token_endpoint": "http://localhost:8081/realms/product-realm/protocol/openid-connect/token",
                        "jwks_uri": "http://localhost:8081/realms/product-realm/protocol/openid-connect/certs",
                        "grant_types_supported": ["client_credentials"],
                        "response_types_supported": ["code"],
                        "subject_types_supported": ["public"],
                        "id_token_signing_alg_values_supported": ["RS256"],
                        "scopes_supported": ["openid", "profile", "product.read"]
                    }
                    """)));

        // Mock Token Endpoint
        authServer.stubFor(WireMock.post(urlEqualTo("/realms/product-realm/protocol/openid-connect/token"))
                .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
            {
                "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                "token_type": "Bearer",
                "expires_in": 300,
                "scope": "openid,profile,product.read"
            }
            """)));

        // Mock Resource Server
        resourceServer.stubFor(WireMock.get(urlEqualTo("/products"))
                .withHeader("Authorization", matching("Bearer.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("""
                    [
                        {"name": "Product 1", "price": 100.00},
                        {"name": "Product 2", "price": 200.00}
                    ]
                    """)));
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> "http://localhost:8081/realms/product-realm");
        registry.add("spring.security.oauth2.client.provider.keycloak.token-uri",
                () -> "http://localhost:8081/realms/product-realm/protocol/openid-connect/token");
        registry.add("spring.security.oauth2.client.registration.keycloak.client-id",
                () -> "test-client");
        registry.add("spring.security.oauth2.client.registration.keycloak.client-secret",
                () -> "test-secret");
        registry.add("spring.security.oauth2.client.registration.keycloak.authorization-grant-type",
                () -> "client_credentials");
        registry.add("spring.security.oauth2.client.registration.keycloak.scope",
                () -> "openid,profile,product.read");
        registry.add("resource-server.url", () -> "http://localhost:8082");
    }

    @Test
    void testGetAllProducts_ShouldReturnAllProductsWithMarkedUpPrices() throws Exception {
        mockMvc.perform(get("/retail/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[0].price").value(120.00))
                .andExpect(jsonPath("$[1].name").value("Product 2"))
                .andExpect(jsonPath("$[1].price").value(240.00));

        authServer.verify(getRequestedFor(urlEqualTo("/realms/product-realm/.well-known/openid-configuration")));
        authServer.verify(postRequestedFor(urlEqualTo("/realms/product-realm/protocol/openid-connect/token")));
        resourceServer.verify(getRequestedFor(urlEqualTo("/products"))
                .withHeader("Authorization", matching("Bearer.*")));
    }
}