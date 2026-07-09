package org.example.secondhandbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="cities")
@NoArgsConstructor
@AllArgsConstructor
public class City {

    @Id // 👈 این انوتیشن دقیقاً جا افتاده است
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 👈 این هم برای اتوایکرومنت شدن شناسه است
    private int id;
    @Column(unique = true)
    private String name;
    public City(String name){
        this.name=name;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
