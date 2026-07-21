package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.service.FavoriteService;
import ir.ac.aut.secondhand.frontend.ui.ProductCard;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.util.List;

public final class FavoritesController {
    private final FavoriteService favoriteService;
    private final UiExecutor uiExecutor;

    @FXML
    private FlowPane favoritesPane;
    @FXML
    private Label stateLabel;

    public FavoritesController(FavoriteService favoriteService, UiExecutor uiExecutor) {
        this.favoriteService = favoriteService;
        this.uiExecutor = uiExecutor;
    }

    @FXML
    private void initialize() {
        loadFavorites();
    }

    @FXML
    private void onRefresh() {
        loadFavorites();
    }

    private void loadFavorites() {
        stateLabel.setText("Loading favorites...");
        favoritesPane.getChildren().clear();
        uiExecutor.run(favoriteService::getFavorites,
                this::render,
                error -> {
                    stateLabel.setText("Unable to load favorites");
                    Alerts.error("Favorites", error);
                });
    }

    private void render(List<ProductDto> products) {
        favoritesPane.getChildren().clear();
        if (products == null || products.isEmpty()) {
            stateLabel.setText("Your favorites list is empty.");
            return;
        }
        stateLabel.setText(products.size() + " favorite advertisement(s)");
        for (ProductDto product : products) {
            ProductCard card = new ProductCard(product);
            Button removeButton = new Button("Remove from favorites");
            removeButton.getStyleClass().add("danger-button");
            removeButton.setMaxWidth(Double.MAX_VALUE);
            removeButton.setOnAction(event -> removeFavorite(product));
            VBox wrapper = new VBox(8, card, removeButton);
            favoritesPane.getChildren().add(wrapper);
        }
    }
    private void removeFavorite(ProductDto product) {
        if (product == null || product.getId() == null) {
            return;
        }
        uiExecutor.runVoid(() -> favoriteService.remove(product.getId()),
                this::loadFavorites,
                error -> Alerts.error("Remove favorite", error));
    }
}
