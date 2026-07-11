
package org.example.secondhandbackend.security;

import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.model.UserType;
import org.example.secondhandbackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("adminPass"))
                    .fullName("System Admin")
                    .phoneNumber("00000000000")
                    .type(UserType.ADMIN)
                    .isActive(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin created -> username: admin | password: adminPass");
        }
    }
}