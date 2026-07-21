package ir.ac.aut.secondhand.frontend.controller;


import ir.ac.aut.secondhand.frontend.dto.ProductImageDto;
import ir.ac.aut.secondhand.frontend.dto.RatingSummaryDto;
import ir.ac.aut.secondhand.frontend.model.enums.ProductStatus;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.AdminService;
import ir.ac.aut.secondhand.frontend.service.ConversationService;
import ir.ac.aut.secondhand.frontend.service.FavoriteService;
import ir.ac.aut.secondhand.frontend.service.ProductService;
import ir.ac.aut.secondhand.frontend.service.RatingService;
import ir.ac.aut.secondhand.frontend.session.UserSession;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.ImageUtils;
import ir.ac.aut.secondhand.frontend.util.MoneyUtils;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import ir.ac.aut.secondhand.frontend.dto.ProductDetailDto;
import ir.ac.aut.secondhand.frontend.model.enums.ProductStatus;
import javafx.scene.layout.HBox;


import java.util.ArrayList;
import java.util.List;

import static ir.ac.aut.secondhand.frontend.navigation.ViewManager.showEditProduct;

public final class ProductDetailController {
    private final ProductService productService;
    private final FavoriteService favoriteService;
    private final ConversationService conversationService;
    private final RatingService ratingService;
    private final AdminService adminService;
    private final UiExecutor uiExecutor;

    private ProductDetailDto product;
    //private ProductDto product;

    @FXML
    private Label titleLabel;
    @FXML
    private HBox thumbnails;
    @FXML
    private Label priceLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label ownerLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label rejectReasonLabel;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private ImageView productImage;
    @FXML
    private Label noImageLabel;
    @FXML
    private Button favoriteButton;
    @FXML
    private Button messageButton;
    @FXML
    private Button rateButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button soldButton;
    @FXML
    private Label stateLabel;

    public ProductDetailController(ProductService productService, FavoriteService favoriteService, ConversationService conversationService, RatingService ratingService, AdminService adminService, UiExecutor uiExecutor) {
        this.productService = productService;
        this.favoriteService = favoriteService;
        this.conversationService = conversationService;
        this.ratingService = ratingService;
        this.adminService = adminService;
        this.uiExecutor = uiExecutor;
    }

    public void loadProduct(long productId) {
        stateLabel.setText("Loading advertisement...");
        uiExecutor.run(() -> productService.getById(productId),
                loaded -> {
                    System.out.println("LOADED PRODUCT = " + loaded);

                    if (loaded != null) {
                        System.out.println("PRODUCT ID = " + loaded.getId());
                        System.out.println("TITLE = " + loaded.getTitle());
                    }

                    this.product = loaded;
                    stateLabel.setText("");
                    render();
                },
                error -> {
                    stateLabel.setText("Unable to load advertisement");
                    Alerts.error("Advertisement details", error);
                });
    }

    @FXML
    private void onBack() {
        ViewManager.showProducts();
    }

    @FXML
    private void onFavorite() {
        if (!ensureProduct()) {
            return;
        }
        uiExecutor.runVoid(() -> favoriteService.add(product.getId()), () -> Alerts.info("Favorites", "Advertisement added to favorites."), error -> Alerts.error("Favorites", error));
    }
    @FXML
    private void onMessage() {
        if (!ensureProduct()) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Message seller");
        dialog.setHeaderText("Start a conversation about “" + product.getTitle() + "”");
        dialog.setContentText("Message:");
        dialog.showAndWait().ifPresent(content -> {
            if (content.isBlank()) {
                Alerts.error("Message", "Message cannot be empty");
                return;
            }
            uiExecutor.run(() -> conversationService.sendMessage(product.getId(), content.trim()),
                    receipt -> {
                        Alerts.info("Message sent", "Your message was sent to the seller.");
                        ViewManager.showConversations(receipt == null ? null : receipt.getConversationId());
                    },
                    error -> Alerts.error("Send message", error));
        });
    }

    @FXML
    private void onRate() {
        if (!ensureProduct()) {
            return;
        }
        Dialog<RatingInput> dialog = createRatingDialog();
        dialog.showAndWait().ifPresent(input ->
                uiExecutor.runVoid(() -> ratingService.rate(product.getId(), input.score(), input.comment()),
                        () -> Alerts.info("Rating", "Your rating was recorded."),
                        error -> Alerts.error("Rating", error)));
    }

    @FXML
    private void onEdit() {
        if (product != null) {
            ViewManager.showEditProduct(product);
            showEditProduct(product);
        }
    }

    @FXML
    private void onDelete() {
        if (!ensureProduct() || !Alerts.confirm("Delete advertisement",
                "Are you sure you want to delete this advertisement?")) {
            return;
        }
        Runnable operation = UserSession.getInstance().isAdmin()
                ? () -> adminService.deleteProduct(product.getId())
                : () -> productService.deleteOwn(product.getId());
        uiExecutor.runVoid(operation,
                () -> {
                    Alerts.info("Advertisement deleted", "The advertisement was deleted.");
                    ViewManager.showProducts();
                },
                error -> Alerts.error("Delete advertisement", error));
    }

    @FXML
    private void onSold() {
        if (!ensureProduct() || !Alerts.confirm("Mark as sold",
                "Mark this advertisement as sold?")) {
            return;
        }
        uiExecutor.runVoid(() -> productService.markSold(product.getId()),
                () -> {
                    Alerts.info("Advertisement sold", "The advertisement is now marked as sold.");
                    loadProduct(product.getId());
                },
                error -> Alerts.error("Mark as sold", error));
    }

    private void render() {
        titleLabel.setText(safe(product.getTitle(), "Untitled advertisement"));
        priceLabel.setText(MoneyUtils.format(product.getPrice()) + " تومان");

        statusLabel.setText(product.getStatus());

        categoryLabel.setText(product.getCategoryName() == null ? "—" : product.getCategoryName());

        cityLabel.setText(product.getCityName() == null ? "—" : product.getCityName());

        String sellerDisplay = product.getSellerFullName() != null && !product.getSellerFullName().isBlank()
                ? product.getSellerFullName()
                : product.getSellerUsername();
        ownerLabel.setText(sellerDisplay == null ? "Unknown seller" : sellerDisplay);
        descriptionArea.setText(safe(product.getDescription(), "No description"));

        String rejectReason = product.getRejectReason();
        rejectReasonLabel.setText(rejectReason == null || rejectReason.isBlank() ? "" : "Rejection reason: " + rejectReason);
        rejectReasonLabel.setVisible(rejectReason != null && !rejectReason.isBlank());
        rejectReasonLabel.setManaged(rejectReasonLabel.isVisible());

        List<Image> images = new ArrayList<>();
        if (product.getImages() != null) {
            for (ProductImageDto productImageDto : product.getImages()) {
                String imagePath = productImageDto.getImagePath();
                if (imagePath != null && !imagePath.isBlank()) {
                    Image img = new Image(imagePath, true);
                    images.add(img);
                    ImageView imageView = new ImageView(img);
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(80);
                    imageView.setPreserveRatio(true);
                    imageView.setOnMouseClicked(event -> {productImage.setImage(img);});
                    thumbnails.getChildren().add(imageView);
                }
            }
        }
        if (!images.isEmpty()) {
            productImage.setImage(images.get(0));
        }
        productImage.setVisible(images != null);
        productImage.setManaged(images != null);
        noImageLabel.setVisible(images == null);
        noImageLabel.setManaged(images == null);

        UserSession session = UserSession.getInstance();
        boolean authenticated = session.isAuthenticated();
        boolean admin = session.isAdmin();

        boolean ownerByUsername = session.username().map(name -> name.equals(product.getSellerUsername())).orElse(false);
        boolean ownerById = session.userId().map(id -> id.equals(product.getId())).orElse(false);
        boolean owner = ownerByUsername || ownerById;
        boolean active = "ACTIVE".equals(product.getStatus());

        setVisibleManaged(favoriteButton, authenticated && !admin && !owner && active);
        setVisibleManaged(messageButton, authenticated && !admin && !owner && active);
        setVisibleManaged(rateButton, authenticated && !admin && !owner && active);
        setVisibleManaged(editButton, authenticated && !admin && owner && !("DELETED".equals(product.getStatus())) && !("SOLD".equals(product.getStatus())));
        setVisibleManaged(soldButton, authenticated && !admin && owner && active);
        setVisibleManaged(deleteButton, authenticated && (admin || owner));

        int sellerId = product.getSellerId();

        if (sellerId > 0) {
            uiExecutor.run(
                    () -> ratingService.getUserRatings(sellerId), this::renderRating, error -> ratingLabel.setText("Rating unavailable")
            );
        } else {
            ratingLabel.setText("No rating yet");
        }
    }
    private void renderRating(RatingSummaryDto summary) {
        if (summary == null || summary.getTotalRatings() == 0) {
            ratingLabel.setText("No rating yet");
            return;
        }
        ratingLabel.setText(String.format("%.1f / 5 (%d rating%s)",
                summary.getAverageScore(), summary.getTotalRatings(),
                summary.getTotalRatings() == 1 ? "" : "s"));
    }
    private Dialog<RatingInput> createRatingDialog() {
        Dialog<RatingInput> dialog = new Dialog<>();
        dialog.setTitle("Rate seller");
        dialog.setHeaderText("Rate the seller of “" + product.getTitle() + "”");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        ComboBox<Integer> score = new ComboBox<>();
        score.getItems().setAll(List.of(1, 2, 3, 4, 5));
        score.setValue(5);
        TextArea comment = new TextArea();
        comment.setPromptText("Optional comment");
        comment.setPrefRowCount(4);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(15));
        grid.addRow(0, new Label("Score:"), score);
        grid.addRow(1, new Label("Comment:"), comment);
        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.disableProperty().bind(score.valueProperty().isNull());
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new RatingInput(score.getValue(), comment.getText())
                : null);
        return dialog;
    }

    private boolean ensureProduct() {
        if (product == null || product.getId() == 0) {
            Alerts.error("Advertisement", "Advertisement data is not available yet");
            return false;
        }
        return true;
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private static void setVisibleManaged(Control control, boolean visible) {
        control.setVisible(visible);
        control.setManaged(visible);
    }

    private record RatingInput(int score, String comment) {
    }
}
