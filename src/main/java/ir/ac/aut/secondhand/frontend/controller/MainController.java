package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.AuthService;
import ir.ac.aut.secondhand.frontend.session.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public final class MainController {
    private final AuthService authService;

    @FXML
    private StackPane contentPane;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label roleLabel;
    @FXML
    private Button favoritesButton;
    @FXML
    private Button conversationsButton;
    @FXML
    private Button newProductButton;
    @FXML
    private Button adminDashboardButton;
    @FXML
    private Button adminPendingButton;
    @FXML
    private Button adminUsersButton;
    @FXML
    private Button adminLookupsButton;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button logoutButton;

    public MainController(AuthService authService) {
        this.authService = authService;
    }

    @FXML
    private void initialize() {
        UserSession session = UserSession.getInstance();
        boolean authenticated = session.isAuthenticated();
        boolean admin = session.isAdmin();
        if (authenticated) {
            usernameLabel.setText(
                    session.username().orElse("User")
            );
            roleLabel.setText(
                    session.getUserType().name()
            );
        } else {
            usernameLabel.setText("Guest");
            roleLabel.setText("");
        }




        setVisibleManaged(loginButton, !authenticated);
        setVisibleManaged(registerButton, !authenticated);
        setVisibleManaged(logoutButton, authenticated);


        setVisibleManaged(favoritesButton, authenticated && !admin);
        setVisibleManaged(conversationsButton, authenticated && !admin);
        setVisibleManaged(newProductButton, authenticated && !admin);


        setVisibleManaged(adminDashboardButton, authenticated && admin);
        setVisibleManaged(adminPendingButton, authenticated && admin);
        setVisibleManaged(adminUsersButton, authenticated && admin);
        setVisibleManaged(adminLookupsButton, authenticated && admin);

        Platform.runLater(() -> {
            if (admin) {
                ViewManager.showAdminDashboard();
            } else {
                ViewManager.showProducts();
            }
        });
    }

    public void setContent(Parent content) {
        contentPane.getChildren().setAll(content);
    }

    @FXML
    private void onProducts() {
        ViewManager.showProducts();
    }

    @FXML
    private void onNewProduct() {
        if (!UserSession.getInstance().isAuthenticated()) {
            ViewManager.showLogin();
            return;
        }
        ViewManager.showNewProduct();
    }

    @FXML
    private void onFavorites() {
        ViewManager.showFavorites();
    }

    @FXML
    private void onConversations() {
        ViewManager.showConversations();
    }

    @FXML
    private void onAdminDashboard() {
        ViewManager.showAdminDashboard();
    }

    @FXML
    private void onAdminPending() {
        ViewManager.showAdminPending();
    }

    @FXML
    private void onAdminUsers() {
        ViewManager.showAdminUsers();
    }

    @FXML
    private void onAdminLookups() {
        ViewManager.showAdminLookups();
    }

    @FXML
    private void onLogout() {
        authService.logout();
        ViewManager.showMain();
    }

    @FXML
    private void onLogin() {
        ViewManager.showLogin();
    }

    @FXML
    private void onRegister() {
        ViewManager.showRegister();
    }

    private static void setVisibleManaged(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }
}
