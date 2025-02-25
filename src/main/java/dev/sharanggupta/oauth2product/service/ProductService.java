package dev.sharanggupta.oauth2product.service;

import dev.sharanggupta.oauth2product.domain.Product;
import dev.sharanggupta.oauth2product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    public List<Product> findAll() {
        return repository.findAll();
    }
    public Product findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }
}
