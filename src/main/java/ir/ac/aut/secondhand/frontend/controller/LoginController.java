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

public final class LoginController {
    private final AuthService authService;
    private final UiExecutor uiExecutor;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label statusLabel;

    public LoginController(AuthService authService, UiExecutor uiExecutor) {
        this.authService = authService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void onLogin() {
        try {
            String username = usernameField.getText();
            String password = passwordField.getText();
            ValidationUtils.requireText(username, "Username");
            ValidationUtils.requireText(password, "Password");
            setBusy(true, "Signing in...");
            uiExecutor.run(() -> authService.login(username.trim(), password),
                    result -> {
                        setBusy(false, "");
                        ViewManager.showMain();
                    },
                    error -> {
                        setBusy(false, "");
                        Alerts.error("Login failed", error);
                    });
        } catch (IllegalArgumentException exception) {
            Alerts.error("Invalid input", exception.getMessage());
        }
    }

    @FXML
    private void onOpenRegister() {
        ViewManager.showRegister();
    }

    private void setBusy(boolean busy, String text) {
        loginButton.setDisable(busy);
        usernameField.setDisable(busy);
        passwordField.setDisable(busy);
        statusLabel.setText(text);
    }
    @FXML
    private void onBack() {
        ViewManager.showMain();
    }
}
