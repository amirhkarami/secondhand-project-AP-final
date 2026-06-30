package org.example.secondhandbackend.model;

import jakarta.persistence.*;//looks like its about some standards about connecting to database
import lombok.*;//it helps by implementing getters and setters
@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder//these three are all about creating the database
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//every object in our database needs and ID to be modified.these two lines helps and generate ids
    private int id;
    @Column(unique=true ,nullable = false) //we cant have an username which is already used
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private UserType type;
}
