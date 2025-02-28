package dev.sharanggupta.oauth2client.service;

import dev.sharanggupta.oauth2client.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {
    private final WebClient webClient;
    private static final BigDecimal MARKUP_PERCENTAGE = new BigDecimal("1.20");

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Product> getProductsWithMarkup() {
        return webClient
                .get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .map(this::applyMarkup)
                .collectList()
                .block();
    }

    private Product applyMarkup(Product product) {
        product.setPrice(product.getPrice().multiply(MARKUP_PERCENTAGE));
        return product;
    }
}
