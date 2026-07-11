
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.City;
import org.example.secondhandbackend.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
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
}