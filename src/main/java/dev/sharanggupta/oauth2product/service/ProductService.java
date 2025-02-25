package dev.sharanggupta.oauth2product.service;

import dev.sharanggupta.oauth2product.domain.Product;
import dev.sharanggupta.oauth2product.exception.ProductNotFoundException;
import dev.sharanggupta.oauth2product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> findAll() {
        return repository.findAll();
    }
    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    public Product create(Product product) {
        return repository.save(product);
    }

    public Product update(Long id, Product product) {
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        return repository.save(existingProduct);
    }

    public void deleteById(Long id) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product " + id + " not found"));
        repository.delete(product);
    }
}
