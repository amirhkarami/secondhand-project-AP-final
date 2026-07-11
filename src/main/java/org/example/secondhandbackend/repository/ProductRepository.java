
package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.Product;
import org.example.secondhandbackend.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByStatus(ProductStatus status);
}