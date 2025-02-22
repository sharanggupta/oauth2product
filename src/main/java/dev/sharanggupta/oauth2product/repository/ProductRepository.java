package dev.sharanggupta.oauth2product.repository;

import dev.sharanggupta.oauth2product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
