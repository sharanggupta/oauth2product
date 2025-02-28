package dev.sharanggupta.oauth2client.oauth2resourceserver.repository;

import dev.sharanggupta.oauth2client.oauth2resourceserver.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {}
