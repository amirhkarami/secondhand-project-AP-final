package ir.ac.aut.secondhand.frontend.controller.admin;

import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.AdminService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.MoneyUtils;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public final class AdminPendingController {
    private final AdminService adminService;
    private final UiExecutor uiExecutor;

    @FXML private TableView<ProductDto> productsTable;
    @FXML private TableColumn<ProductDto, Number> idColumn;
    @FXML private TableColumn<ProductDto, String> titleColumn;
    @FXML private TableColumn<ProductDto, String> ownerColumn;
    @FXML private TableColumn<ProductDto, String> priceColumn;
    @FXML private TableColumn<ProductDto, String> categoryColumn;
    @FXML private TableColumn<ProductDto, String> cityColumn;
    @FXML private Label stateLabel;

    public AdminPendingController(AdminService adminService, UiExecutor uiExecutor) {
        this.adminService = adminService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getId()));
        titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitle()));
        ownerColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getOwnerUsername()));
        priceColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(MoneyUtils.format(data.getValue().getPrice())));
        categoryColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getCategoryName() != null
                        ? data.getValue().getCategoryName()
                        : "—"));
        cityColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getCityName() != null
                        ? data.getValue().getCityName()
                        : "—"));
        productsTable.setRowFactory(table -> {
            TableRow<ProductDto> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty() && row.getItem().getId() != null) {
                    ViewManager.showProductDetail(row.getItem().getId());
                }
            });
            return row;
        });
        loadPending();
    }

    @FXML
    private void onRefresh() {
        loadPending();
    }

    @FXML
    private void onView() {
        ProductDto selected = requireSelection();
        if (selected != null && selected.getId() != null) {
            ViewManager.showProductDetail(selected.getId());
        }
    }

    @FXML
    private void onApprove() {
        ProductDto selected = requireSelection();
        if (selected == null) {
            return;
        }
        uiExecutor.runVoid(() -> adminService.approveProduct(selected.getId()),
                () -> {
                    Alerts.info("Approved", "Advertisement approved.");
                    loadPending();
                },
                error -> Alerts.error("Approve advertisement", error));
    }

    @FXML
    private void onReject() {
        ProductDto selected = requireSelection();
        if (selected == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reject advertisement");
        dialog.setHeaderText(selected.getTitle());
        dialog.setContentText("Reason:");
        dialog.showAndWait().ifPresent(reason -> {
            if (reason.isBlank()) {
                Alerts.error("Reject advertisement", "Reason cannot be empty");
                return;
            }
            uiExecutor.runVoid(() -> adminService.rejectProduct(selected.getId(), reason.trim()),
                    () -> {
                        Alerts.info("Rejected", "Advertisement rejected.");
                        loadPending();
                    },
                    error -> Alerts.error("Reject advertisement", error));
        });
    }

    @FXML
    private void onDelete() {
        ProductDto selected = requireSelection();
        if (selected == null || !Alerts.confirm("Delete advertisement",
                "Delete “" + selected.getTitle() + "”?")) {
            return;
        }
        uiExecutor.runVoid(() -> adminService.deleteProduct(selected.getId()),
                () -> {
                    Alerts.info("Deleted", "Advertisement deleted.");
                    loadPending();
                },
                error -> Alerts.error("Delete advertisement", error));
    }

    private ProductDto requireSelection() {
        ProductDto selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alerts.error("Selection required", "Select an advertisement first");
        }
        return selected;
    }

    private void loadPending() {
        stateLabel.setText("Loading pending advertisements...");
        uiExecutor.run(adminService::getPendingProducts,
                products -> {
                    List<ProductDto> safe = products == null ? List.of() : products;
                    productsTable.getItems().setAll(safe);
                    stateLabel.setText(safe.isEmpty() ? "No pending advertisements" : safe.size() + " pending advertisement(s)");
                },
                error -> {
                    stateLabel.setText("Unable to load pending advertisements");
                    Alerts.error("Pending advertisements", error);
                });
    }
}
