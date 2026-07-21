package ir.ac.aut.secondhand.frontend.ui;

import ir.ac.aut.secondhand.frontend.dto.ProductDto;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.util.ImageUtils;
import ir.ac.aut.secondhand.frontend.util.MoneyUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class ProductCard extends VBox {
    private final ProductDto product;

    public ProductCard(ProductDto product) {
        this.product = product;
        getStyleClass().add("product-card");
        setSpacing(8);
        setAlignment(Pos.TOP_LEFT);
        setPrefWidth(230);
        setMinWidth(210);
        setMaxWidth(250);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(210);
        imageView.setFitHeight(145);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("product-image");
        //Image image = product.getImages().isEmpty() ? null : ImageUtils.toImage(product.getImages().getFirst());

        Image image = null;

        if (product.getImagePath() != null && !product.getImagePath().isBlank()) {

            image = new Image(
                      product.getImagePath(),
                    true
            );
        }

        if (image != null) {
            imageView.setImage(image);
        } else {
            Region placeholder = new Region();
            placeholder.setPrefSize(210, 145);
            placeholder.getStyleClass().add("image-placeholder");
            getChildren().add(placeholder);
        }

        if (image != null) {
            getChildren().add(imageView);
        }

        Label title = new Label(product.getTitle());
        title.getStyleClass().add("product-title");
        title.setWrapText(true);

        Label price = new Label(MoneyUtils.format(product.getPrice()) + " تومان");
        price.getStyleClass().add("product-price");

        String location = product.getCityName() != null
                ? product.getCityName()
                : "Unknown city";
        String category = product.getCategoryName() != null
                ? product.getCategoryName()
                : "Uncategorized";
        Label meta = new Label(location + " • " + category);
        meta.getStyleClass().add("muted-label");
        meta.setWrapText(true);

        getChildren().addAll(title, price, meta);
        setOnMouseClicked(event -> {
            if (product.getId() != null) {
                ViewManager.showProductDetail(product.getId());
            }
        });
    }

    public ProductDto getProduct() {
        return product;
    }
}
