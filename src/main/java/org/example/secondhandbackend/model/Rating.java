package org.example.secondhandbackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ratings", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "buyer_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(nullable = false)
    private int score;

    @Column(length = 500)
    private String comment;
}