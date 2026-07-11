package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    //I added this method so we can check if we have duplicate categories
    Optional<Category> findByName(String name);
}