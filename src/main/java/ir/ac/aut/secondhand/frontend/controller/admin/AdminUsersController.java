package ir.ac.aut.secondhand.frontend.controller.admin;

import ir.ac.aut.secondhand.frontend.dto.UserDto;
import ir.ac.aut.secondhand.frontend.model.enums.UserType;
import ir.ac.aut.secondhand.frontend.service.AdminService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public final class AdminUsersController {
    private final AdminService adminService;
    private final UiExecutor uiExecutor;

    @FXML private TableView<UserDto> usersTable;
    @FXML private TableColumn<UserDto, Number> idColumn;
    @FXML private TableColumn<UserDto, String> usernameColumn;
    @FXML private TableColumn<UserDto, String> nameColumn;
    @FXML private TableColumn<UserDto, String> phoneColumn;
    @FXML private TableColumn<UserDto, String> roleColumn;
    @FXML private TableColumn<UserDto, String> activeColumn;
    @FXML private Label stateLabel;

    public AdminUsersController(AdminService adminService, UiExecutor uiExecutor) {
        this.adminService = adminService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        usernameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getUsername()));
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getFullName()));
        phoneColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPhoneNumber()));
        roleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType().name()));
        activeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().isActive() ? "Active" : "Blocked"));
        loadUsers();
    }

    @FXML
    private void onRefresh() {
        loadUsers();
    }

    @FXML
    private void onBlock() {
        UserDto selected = requireSelection();
        if (selected == null) {
            return;
        }
        if (selected.getType() == UserType.ADMIN) {
            Alerts.error("Block user", "Administrators cannot be blocked");
            return;
        }
        if (!selected.isActive()) {
            Alerts.error("Block user", "This user is already blocked");
            return;
        }
        uiExecutor.runVoid(() -> adminService.blockUser(selected.getId()),
                () -> {
                    Alerts.info("User blocked", selected.getUsername() + " was blocked.");
                    loadUsers();
                },
                error -> Alerts.error("Block user", error));
    }

    @FXML
    private void onUnblock() {
        UserDto selected = requireSelection();
        if (selected == null) {
            return;
        }
        if (selected.isActive()) {
            Alerts.error("Unblock user", "This user is already active");
            return;
        }
        uiExecutor.runVoid(() -> adminService.unblockUser(selected.getId()),
                () -> {
                    Alerts.info("User unblocked", selected.getUsername() + " was unblocked.");
                    loadUsers();
                },
                error -> Alerts.error("Unblock user", error));
    }

    @FXML
    private void onPromote() {
        UserDto selected = requireSelection();
        if (selected == null) {
            return;
        }
        if (selected.getType() == UserType.ADMIN) {
            Alerts.error("Promote user", "This user is already an administrator");
            return;
        }
        if (!Alerts.confirm("Promote user", "Promote “" + selected.getUsername() + "” to administrator?")) {
            return;
        }
        uiExecutor.runVoid(() -> adminService.promoteUser(selected.getId()),
                () -> {
                    Alerts.info("User promoted", selected.getUsername() + " is now an administrator.");
                    loadUsers();
                },
                error -> Alerts.error("Promote user", error));
    }

    private UserDto requireSelection() {
        UserDto selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("Selection required", "Select a user first");
        } else if (selected.getId() == null) {
            Alerts.error("User data", "Selected user has no id in backend response");
            return null;
        }
        return selected;
    }

    private void loadUsers() {
        stateLabel.setText("Loading users...");
        uiExecutor.run(adminService::getUsers,
                users -> {
                    List<UserDto> safe = users == null ? List.of() : users;
                    usersTable.getItems().setAll(safe);
                    stateLabel.setText(safe.size() + " user(s)");
                },
                error -> {
                    stateLabel.setText("Unable to load users");
                    Alerts.error("Users", error);
                });
    }
}
