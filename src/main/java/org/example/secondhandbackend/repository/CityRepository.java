package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    //I added this method for checking if we have duplicate cities
    Optional<City> findByName(String name);
}