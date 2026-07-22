package org.example.secondhandbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title; // به جای header

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column
    private long price; // تغییر از int به long

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status; // تغییر از productStatus به ProductStatus

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProductImage> images = new ArrayList<>();
    @Column(length = 500)
    private String rejectReason;
}