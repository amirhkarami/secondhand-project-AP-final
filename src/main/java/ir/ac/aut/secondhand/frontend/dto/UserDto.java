package ir.ac.aut.secondhand.frontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ir.ac.aut.secondhand.frontend.model.enums.UserType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Integer id;
    private String username;
    private String fullName;
    private String phoneNumber;

    @JsonAlias({"type", "role", "userType"})
    private UserType type = UserType.UNKNOWN;

    @JsonAlias({"active", "isActive"})
    private boolean active = true;

    public UserDto() {
    }

    public UserDto(Integer id, String username, String fullName, String phoneNumber,
                   UserType type, boolean active) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.active = active;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type == null ? UserType.UNKNOWN : type;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return username == null ? "Unknown user" : username;
    }
}
