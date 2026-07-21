package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.dto.CategoryDto;
import ir.ac.aut.secondhand.frontend.dto.CityDto;
import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.dto.ProductSearchCriteria;
import ir.ac.aut.secondhand.frontend.model.enums.SortOption;
import ir.ac.aut.secondhand.frontend.service.LookupService;
import ir.ac.aut.secondhand.frontend.service.ProductService;
import ir.ac.aut.secondhand.frontend.ui.ProductCard;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import ir.ac.aut.secondhand.frontend.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

import java.util.List;

public final class ProductListController {
    private final ProductService productService;
    private final LookupService lookupService;
    private final UiExecutor uiExecutor;

    @FXML
    private TextField keywordField;
    @FXML
    private TextField minPriceField;
    @FXML
    private TextField maxPriceField;
    @FXML
    private ComboBox<CategoryDto> categoryCombo;
    @FXML
    private ComboBox<CityDto> cityCombo;
    @FXML
    private ComboBox<SortOption> sortCombo;
    @FXML
    private FlowPane productsPane;
    @FXML
    private Label stateLabel;

    public ProductListController(ProductService productService, LookupService lookupService,
                                 UiExecutor uiExecutor) {
        this.productService = productService;
        this.lookupService = lookupService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        sortCombo.getItems().setAll(SortOption.values());
        sortCombo.setValue(SortOption.NEWEST);
        loadLookups();
        loadProducts(null);
    }

    @FXML
    private void onSearch() {
        try {
            Long min = ValidationUtils.parseNullableLong(minPriceField.getText(), "Minimum price");
            Long max = ValidationUtils.parseNullableLong(maxPriceField.getText(), "Maximum price");
            if (min != null && max != null && min > max) {
                throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
            }
            CategoryDto category = categoryCombo.getValue();
            CityDto city = cityCombo.getValue();
            ProductSearchCriteria criteria = new ProductSearchCriteria(
                    keywordField.getText(),
                    category == null ? null : category.getId(),
                    city == null ? null : city.getId(),
                    min,
                    max,
                    sortCombo.getValue()
            );
            loadProducts(criteria);
        } catch (IllegalArgumentException exception) {
            Alerts.error("Invalid filters", exception.getMessage());
        }
    }

    @FXML
    private void onClear() {
        keywordField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        categoryCombo.setValue(null);
        cityCombo.setValue(null);
        sortCombo.setValue(SortOption.NEWEST);
        loadProducts(null);
    }

    @FXML
    private void onRefresh() {
        onSearch();
    }

    private void loadLookups() {
        uiExecutor.run(lookupService::getCategories,
                categories -> categoryCombo.getItems().setAll(categories),
                error -> Alerts.error("Categories", error));
        uiExecutor.run(lookupService::getCities,
                cities -> cityCombo.getItems().setAll(cities),
                error -> Alerts.error("Cities", error));
    }

    private void loadProducts(ProductSearchCriteria criteria) {
        stateLabel.setText("Loading advertisements...");
        productsPane.getChildren().clear();
        uiExecutor.run(
                () -> criteria == null ? productService.getActiveProducts() : productService.search(criteria),
                this::renderProducts,
                error -> {
                    stateLabel.setText("Unable to load advertisements");
                    Alerts.error("Advertisements", error);
                });
    }

    private void renderProducts(List<ProductDto> products) {
        productsPane.getChildren().clear();
        if (products == null || products.isEmpty()) {
            stateLabel.setText("No advertisements matched your filters.");
            return;
        }
        stateLabel.setText(products.size() + " advertisement(s)");
        products.stream().map(ProductCard::new).forEach(productsPane.getChildren()::add);
    }
}
