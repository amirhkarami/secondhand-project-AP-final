
package org.example.secondhandbackend.service;

import org.example.secondhandbackend.dto.UserSummaryDto;
import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.LoginRequest;
import org.example.secondhandbackend.model.RegisterRequest;
import org.example.secondhandbackend.model.User;
import org.example.secondhandbackend.model.UserType;
import org.example.secondhandbackend.repository.UserRepository;
import org.example.secondhandbackend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(RegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ApiException("username can not be empty", 400);
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ApiException("password can not be empty", 400);
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ApiException("this username is already used", 400);
        }
        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .type(UserType.USER)
                .isActive(true)
                .build();
        userRepository.save(newUser);

        return "SUCCESS";
    }

    public String loginUser(LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new ApiException("User not found", 404);
        }
        User user = userOptional.get();

        if (!user.isActive()) {
            throw new ApiException("User is not active", 403);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Username or password is invalid", 401);
        }

        return jwtUtil.generateToken(user.getUsername(), user.getType().name());
    }

    public List<UserSummaryDto> getAllUsers(String adminUsername) {
        checkAdmin(adminUsername);
        return userRepository.findAll().stream()
                .map(u -> new UserSummaryDto(u.getId(), u.getUsername(), u.getFullName(), u.getType().name(), u.isActive()))
                .collect(java.util.stream.Collectors.toList());
    }

    public void blockUser(int targetId, String adminUsername) {
        checkAdmin(adminUsername);
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ApiException("user not found", 404));

        if (target.getType() == UserType.ADMIN) {
            throw new ApiException("you cannot block an admin", 400);
        }
        if (!target.isActive()) {
            throw new ApiException("this user is already blocked", 400);
        }
        target.setActive(false);
        userRepository.save(target);
    }

    public void unblockUser(int targetId, String adminUsername) {
        checkAdmin(adminUsername);
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new ApiException("user not found", 404));

        if (target.isActive()) {
            throw new ApiException("user is already unblocked", 400);
        }
        target.setActive(true);
        userRepository.save(target);
    }

    public void promoteToAdmin(int targetUserId, String requesterUsername) {
        checkAdmin(requesterUsername);
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ApiException("user not found", 404));

        if (target.getType() == UserType.ADMIN) {
            throw new ApiException("this user is already an admin", 400);
        }
        target.setType(UserType.ADMIN);
        userRepository.save(target);
    }

    private void checkAdmin(String username) {
        User requester = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("user not found", 404));
        if (requester.getType() != UserType.ADMIN) {
            throw new ApiException("you are not admin", 403);
        }
    }


}