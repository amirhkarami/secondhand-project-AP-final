package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.City;
import org.example.secondhandbackend.repository.CityRepository;
import org.example.secondhandbackend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final ProductRepository productRepository;

    public CityService(CityRepository cityRepository, ProductRepository productRepository) {
        this.cityRepository = cityRepository;
        this.productRepository = productRepository;
    }

    public List<City> getAll() {
        return cityRepository.findAll();
    }

    public City create(String name) {
        if (name == null || name.isBlank()) {
            throw new ApiException("city name cannot be empty", 400);
        }
        City city = new City(name);
        return cityRepository.save(city);
    }

    public void delete(int id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ApiException("city not found", 404));

        boolean usedByProducts = productRepository.findAll().stream()
                .anyMatch(p -> p.getCity() != null && p.getCity().getId() == id);

        if (usedByProducts) {
            throw new ApiException("this city is used by existing advertisements and cannot be deleted", 400);
        }

        cityRepository.deleteById(id);
    }



}