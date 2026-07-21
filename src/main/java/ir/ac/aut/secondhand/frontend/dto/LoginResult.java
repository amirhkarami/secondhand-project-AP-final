package ir.ac.aut.secondhand.frontend.dto;

import ir.ac.aut.secondhand.frontend.model.enums.UserType;

public class LoginResult {
    private final String token;
    private final String username;
    private final Integer userId;
    private final UserType userType;

    public LoginResult(String token, String username, Integer userId, UserType userType) {
        this.token = token;
        this.username = username;
        this.userId = userId;
        this.userType = userType;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public Integer getUserId() {
        return userId;
    }

    public UserType getUserType() {
        return userType;
    }
}
