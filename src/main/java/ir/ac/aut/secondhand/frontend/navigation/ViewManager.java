package ir.ac.aut.secondhand.frontend.navigation;

import ir.ac.aut.secondhand.frontend.context.AppContext;
import ir.ac.aut.secondhand.frontend.controller.ConversationsController;
import ir.ac.aut.secondhand.frontend.controller.MainController;
import ir.ac.aut.secondhand.frontend.controller.ProductDetailController;
import ir.ac.aut.secondhand.frontend.controller.ProductFormController;
import ir.ac.aut.secondhand.frontend.dto.ProductDetailDto;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public final class ViewManager {
    private static Stage stage;
    private static AppContext context;
    private static MainController mainController;

    private ViewManager() {
    }

    public static void initialize(Stage primaryStage, AppContext appContext) {
        stage = primaryStage;
        context = appContext;
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setTitle("Second Hand Marketplace");
    }

    public static void showLogin() {
        mainController = null;
        setScene(load("/fxml/login.fxml").getRoot(), 1050, 720);
    }

    public static void showRegister() {
        mainController = null;
        setScene(load("/fxml/register.fxml").getRoot(), 1050, 760);
    }

    public static void showMain() {
        ViewBundle<MainController> bundle = load("/fxml/main.fxml");
        mainController = bundle.getController();
        setScene(bundle.getRoot(), 1280, 800);
    }

    public static void showProducts() {
        showInMain("/fxml/product-list.fxml");
    }

    public static void showNewProduct() {
        ViewBundle<ProductFormController> bundle = load("/fxml/product-form.fxml");
        bundle.getController().setEditingProduct(null);
        setMainContent(bundle.getRoot());
    }

    public static void showEditProduct(ProductDetailDto product) {
        ViewBundle<ProductFormController> bundle = load("/fxml/product-form.fxml");
        bundle.getController().setEditingProduct(product);
        setMainContent(bundle.getRoot());
    }

    public static void showProductDetail(long productId) {
        ViewBundle<ProductDetailController> bundle = load("/fxml/product-detail.fxml");
        bundle.getController().loadProduct(productId);
        setMainContent(bundle.getRoot());
    }

    public static void showFavorites() {
        showInMain("/fxml/favorites.fxml");
    }

    public static void showConversations() {
        showConversations(null);
    }

    public static void showConversations(Long conversationId) {
        ViewBundle<ConversationsController> bundle = load("/fxml/conversations.fxml");
        bundle.getController().setConversationToSelect(conversationId);
        setMainContent(bundle.getRoot());
    }

    public static void showAdminDashboard() {
        showInMain("/fxml/admin-dashboard.fxml");
    }

    public static void showAdminPending() {
        showInMain("/fxml/admin-pending.fxml");
    }

    public static void showAdminUsers() {
        showInMain("/fxml/admin-users.fxml");
    }

    public static void showAdminLookups() {
        showInMain("/fxml/admin-lookups.fxml");
    }

    public static void showInMain(String fxml) {
        setMainContent(load(fxml).getRoot());
    }

    private static void setMainContent(Parent root) {
        if (mainController == null) {
            throw new IllegalStateException("Main view is not active");
        }
        mainController.setContent(root);
    }
    private static void setScene(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        URL css = ViewManager.class.getResource("/css/app.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    @SuppressWarnings("unchecked")
    public static <T> ViewBundle<T> load(String resource) {
        try {
            URL url = ViewManager.class.getResource(resource);
            if (url == null) {
                throw new IOException("FXML not found: " + resource);
            }
            FXMLLoader loader = new FXMLLoader(url);
            loader.setControllerFactory(context::createController);
            Parent root = loader.load();
            return new ViewBundle<>(root, (T) loader.getController());
        } catch (IOException exception) {
            Alerts.error("View error", exception);
            throw new IllegalStateException("Unable to load view " + resource, exception);
        }
    }
}
