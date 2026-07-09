package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.model.City;
import org.example.secondhandbackend.repository.CityRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CityController {

    private final CityRepository cityRepository;
    //Bean Injection
    public CityController(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @GetMapping("/cities")
    public List<City> getAllCities() {
        return cityRepository.findAll();
    }
}