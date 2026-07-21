package ir.ac.aut.secondhand.frontend.session;

import ir.ac.aut.secondhand.frontend.model.enums.UserType;

import java.util.Objects;
import java.util.Optional;

public final class UserSession {
    private static final UserSession INSTANCE = new UserSession();

    private String token;
    private String username;
    private Integer userId;
    private UserType userType = UserType.UNKNOWN;

    private UserSession() {
    }

    public static UserSession getInstance() {
        return INSTANCE;
    }

    public synchronized void start(String token, String username, Integer userId, UserType userType) {
        this.token = Objects.requireNonNull(token, "token");
        this.username = username;
        this.userId = userId;
        this.userType = userType == null ? UserType.UNKNOWN : userType;
    }

    public synchronized void clear() {
        token = null;
        username = null;
        userId = null;
        userType = UserType.UNKNOWN;
    }

    public synchronized boolean isAuthenticated() {
        return token != null && !token.isBlank();
    }

    public synchronized Optional<String> token() {
        return Optional.ofNullable(token);
    }

    public synchronized Optional<String> username() {
        return Optional.ofNullable(username);
    }

    public synchronized Optional<Integer> userId() {
        return Optional.ofNullable(userId);
    }

    public synchronized UserType getUserType() {
        return userType;
    }

    public synchronized boolean isAdmin() {
        return userType == UserType.ADMIN;
    }
}
