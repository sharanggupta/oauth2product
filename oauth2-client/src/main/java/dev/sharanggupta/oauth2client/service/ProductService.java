package dev.sharanggupta.oauth2client.service;

import dev.sharanggupta.oauth2client.domain.Product;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProductService {

    private final WebClient webClient;
    private static final BigDecimal MARKUP_PERCENTAGE = new BigDecimal("20.0");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Product> getProductsWithMarkup() {
        return webClient.get()
                .uri("/products")
                .retrieve()
                .bodyToFlux(Product.class)
                .map(this::applyMarkup)
                .collectList()
                .block();
    }

    private Product applyMarkup(Product product) {
        BigDecimal markup = MARKUP_PERCENTAGE.divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP)
                .add(BigDecimal.ONE);
        BigDecimal markedUpPrice = product.price().multiply(markup)
                .setScale(2, RoundingMode.HALF_UP);
        return new Product(product.name(), markedUpPrice);
    }
}