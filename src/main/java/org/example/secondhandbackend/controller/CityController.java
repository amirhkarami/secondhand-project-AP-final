
package org.example.secondhandbackend.controller;

import org.example.secondhandbackend.model.City;
import org.example.secondhandbackend.service.CityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/cities")
    public List<City> getAllCities() {
        return cityService.getAll();
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody Map<String, String> body) {
        City city = cityService.create(body.get("name"));
        return ResponseEntity.ok(city);
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable int id) {
        cityService.delete(id);
        return new ResponseEntity<>("city deleted", HttpStatus.OK);
    }
}