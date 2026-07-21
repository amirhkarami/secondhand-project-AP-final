package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.AuthService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import ir.ac.aut.secondhand.frontend.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public final class RegisterController {
    private final AuthService authService;
    private final UiExecutor uiExecutor;

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label statusLabel;

    public RegisterController(AuthService authService, UiExecutor uiExecutor) {
        this.authService = authService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void onRegister() {
        try {
            String fullName = fullNameField.getText();
            String username = usernameField.getText();
            String phone = phoneField.getText();
            String password = passwordField.getText();
            String confirm = confirmPasswordField.getText();

            ValidationUtils.requireText(fullName, "Full name");
            ValidationUtils.requireText(username, "Username");
            ValidationUtils.requireText(phone, "Phone number");
            ValidationUtils.requirePassword(password);
            if (!password.equals(confirm)) {
                throw new IllegalArgumentException("Password confirmation does not match");
            }

            setBusy(true, "Creating account...");
            uiExecutor.runVoid(
                    () -> authService.register(username.trim(), password, fullName.trim(), phone.trim()),
                    () -> {
                        setBusy(false, "");
                        Alerts.info("Registration successful", "Your account was created. You can now sign in.");
                        ViewManager.showLogin();
                    },
                    error -> {
                        setBusy(false, "");
                        Alerts.error("Registration failed", error);
                    });
        } catch (IllegalArgumentException exception) {
            Alerts.error("Invalid input", exception.getMessage());
        }
    }

    @FXML
    private void onBackToLogin() {
        ViewManager.showLogin();
    }

    private void setBusy(boolean busy, String message) {
        registerButton.setDisable(busy);
        statusLabel.setText(message);
    }
    @FXML
    private void onBack() {
        ViewManager.showMain();
    }
}
