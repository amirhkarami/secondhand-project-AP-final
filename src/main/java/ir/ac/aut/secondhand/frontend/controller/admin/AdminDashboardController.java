package ir.ac.aut.secondhand.frontend.controller.admin;

import ir.ac.aut.secondhand.frontend.dto.DashboardDto;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.AdminService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public final class AdminDashboardController {
    private final AdminService adminService;
    private final UiExecutor uiExecutor;

    @FXML private Label totalUsersLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label pendingProductsLabel;
    @FXML private Label activeProductsLabel;
    @FXML private Label blockedUsersLabel;
    @FXML private Label stateLabel;

    public AdminDashboardController(AdminService adminService, UiExecutor uiExecutor) {
        this.adminService = adminService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        loadDashboard();
    }

    @FXML
    private void onRefresh() {
        loadDashboard();
    }

    @FXML
    private void onPending() {
        ViewManager.showAdminPending();
    }

    @FXML
    private void onUsers() {
        ViewManager.showAdminUsers();
    }

    private void loadDashboard() {
        stateLabel.setText("Loading dashboard...");
        uiExecutor.run(adminService::getDashboard,
                this::render,
                error -> {
                    stateLabel.setText("Unable to load dashboard");
                    Alerts.error("Admin dashboard", error);
                });
    }

    private void render(DashboardDto dashboard) {
        stateLabel.setText("");
        totalUsersLabel.setText(Integer.toString(dashboard.getTotalUsers()));
        totalProductsLabel.setText(Integer.toString(dashboard.getTotalProducts()));
        pendingProductsLabel.setText(Integer.toString(dashboard.getPendingProducts()));
        activeProductsLabel.setText(Integer.toString(dashboard.getActiveProducts()));
        blockedUsersLabel.setText(Integer.toString(dashboard.getBlockedUsers()));
    }
}
