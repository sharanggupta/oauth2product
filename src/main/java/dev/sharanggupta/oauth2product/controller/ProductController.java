package dev.sharanggupta.oauth2product.controller;

import dev.sharanggupta.oauth2product.domain.Product;
import dev.sharanggupta.oauth2product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
@PreAuthorize("hasRole('USER')")
public class ProductController {
    @Autowired
    private ProductService service;
    @GetMapping
    public List<Product> getAllProducts() {
        return service.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }
}