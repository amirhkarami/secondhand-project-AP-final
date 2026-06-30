package org.example.secondhandbackend.service;
import org.example.secondhandbackend.model.LoginRequest;
import org.example.secondhandbackend.model.RegisterRequest;
import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.model.UserType;
import org.example.secondhandbackend.repository.UserRepository;
import org.example.secondhandbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class UserService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }
    public String registerUser(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return "ERROR";
        }
        User newUser = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .type(UserType.USER)
                .build();
        userRepository.save(newUser);

        return "SUCCESS";
    }
    public String loginUser(LoginRequest request) {

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            return "ERROR";
        }
        User user = userOptional.get();
        if (!user.getPassword().equals(request.getPassword())) {
            return "ERROR";
        }
        return jwtUtil.generateToken(user.getUsername());
    }
}