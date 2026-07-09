package org.example.secondhandbackend.repository;

import org.example.secondhandbackend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    // یک متد کاربردی: پیدا کردن شهر از روی نام آن (برای مواقعی که می‌خواهید بررسی کنید شهر تکراری نباشد)
    Optional<City> findByName(String name);
}