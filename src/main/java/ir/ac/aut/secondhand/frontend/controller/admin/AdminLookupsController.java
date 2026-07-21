package ir.ac.aut.secondhand.frontend.controller.admin;

import ir.ac.aut.secondhand.frontend.dto.CategoryDto;
import ir.ac.aut.secondhand.frontend.dto.CityDto;
import ir.ac.aut.secondhand.frontend.service.LookupService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public final class AdminLookupsController {
    private final LookupService lookupService;
    private final UiExecutor uiExecutor;

    @FXML private ListView<CategoryDto> categoriesList;
    @FXML private TextField categoryIdField;
    @FXML private TextField categoryNameField;
    @FXML private ComboBox<CategoryDto> superCategoryCombo;
    @FXML private ListView<CityDto> citiesList;
    @FXML private TextField cityNameField;
    @FXML private Label stateLabel;

    public AdminLookupsController(LookupService lookupService, UiExecutor uiExecutor) {
        this.lookupService = lookupService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        loadAll();
    }

    @FXML
    private void onRefresh() {
        loadAll();
    }

    @FXML
    private void onCreateCategory() {
        String name = categoryNameField.getText();
        if (name == null || name.isBlank()) {
            Alerts.error("Category", "Category name cannot be empty");
            return;
        }
        Long categoryId = null;
        String idText = categoryIdField.getText();
        if (idText != null && !idText.isBlank()) {
            try {
                categoryId = Long.parseLong(idText.trim());
                if (categoryId <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                Alerts.error("Category", "Category ID must be a positive integer");
                return;
            }
        }
        CategoryDto parent = superCategoryCombo.getValue();
        Long finalCategoryId = categoryId;
        uiExecutor.runVoid(() -> lookupService.createCategory(finalCategoryId, name.trim(), parent),
                () -> {
                    categoryIdField.clear();
                    categoryNameField.clear();
                    superCategoryCombo.setValue(null);
                    Alerts.info("Category", "Category created.");
                    loadCategories();
                },
                error -> Alerts.error("Create category", error));
    }

    @FXML
    private void onDeleteCategory() {
        CategoryDto selected = categoriesList.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            Alerts.error("Category", "Select a category first");
            return;
        }
        if (!Alerts.confirm("Delete category", "Delete “" + selected.getName() + "”?")) {
            return;
        }
        uiExecutor.runVoid(() -> lookupService.deleteCategory(selected.getId()),
                () -> {
                    Alerts.info("Category", "Category deleted.");
                    loadCategories();
                },
                error -> Alerts.error("Delete category", error));
    }

    @FXML
    private void onCreateCity() {
        String name = cityNameField.getText();
        if (name == null || name.isBlank()) {
            Alerts.error("City", "City name cannot be empty");
            return;
        }
        uiExecutor.runVoid(() -> lookupService.createCity(name.trim()),
                () -> {
                    cityNameField.clear();
                    Alerts.info("City", "City created.");
                    loadCities();
                },
                error -> Alerts.error("Create city", error));
    }

    @FXML
    private void onDeleteCity() {
        CityDto selected = citiesList.getSelectionModel().getSelectedItem();
        if (selected == null || selected.getId() == null) {
            Alerts.error("City", "Select a city first");
            return;
        }
        if (!Alerts.confirm("Delete city", "Delete “" + selected.getName() + "”?")) {
            return;
        }
        uiExecutor.runVoid(() -> lookupService.deleteCity(selected.getId()),
                () -> {
                    Alerts.info("City", "City deleted.");
                    loadCities();
                },
                error -> Alerts.error("Delete city", error));
    }

    private void loadAll() {
        loadCategories();
        loadCities();
    }

    private void loadCategories() {
        stateLabel.setText("Loading categories and cities...");
        uiExecutor.run(lookupService::getCategories,
                categories -> {
                    List<CategoryDto> safe = categories == null ? List.of() : categories;
                    categoriesList.getItems().setAll(safe);
                    superCategoryCombo.getItems().setAll(safe);
                    stateLabel.setText("");
                },
                error -> Alerts.error("Categories", error));
    }

    private void loadCities() {
        uiExecutor.run(lookupService::getCities,
                cities -> citiesList.getItems().setAll(cities == null ? List.of() : cities),
                error -> Alerts.error("Cities", error));
    }
}
