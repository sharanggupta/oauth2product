package dev.sharanggupta.oauth2client.controller;

import dev.sharanggupta.oauth2client.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    private WebClient webClient;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
    }

    @Test
    void shouldReturnProductsWithMarkedUpPrices() {
        // Given
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("100.00"));

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("200.00"));

        List<Product> originalProducts = Arrays.asList(product1, product2);

        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/products")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Product.class)).thenReturn(Flux.fromIterable(originalProducts));

        // When & Then
        webTestClient.get()
                .uri("/retail/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .value(products -> {
                    assertThat(products).hasSize(2);
                    assertThat(products.get(0).getPrice()).isEqualTo(new BigDecimal("120.00"));
                    assertThat(products.get(1).getPrice()).isEqualTo(new BigDecimal("240.00"));
                });
    }
}