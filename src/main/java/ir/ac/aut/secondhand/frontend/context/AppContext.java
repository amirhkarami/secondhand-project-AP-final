package ir.ac.aut.secondhand.frontend.context;

import ir.ac.aut.secondhand.frontend.client.ApiClient;
import ir.ac.aut.secondhand.frontend.config.AppConfig;
import ir.ac.aut.secondhand.frontend.controller.*;
import ir.ac.aut.secondhand.frontend.controller.admin.*;
import ir.ac.aut.secondhand.frontend.service.*;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;

public final class AppContext implements AutoCloseable {
    private final AppConfig config;
    private final ApiClient apiClient;
    private final UiExecutor uiExecutor;

    private final AuthService authService;
    private final ProductService productService;
    private final AdminService adminService;
    private final LookupService lookupService;
    private final FavoriteService favoriteService;
    private final ConversationService conversationService;
    private final RatingService ratingService;

    public AppContext() {
        this.config = new AppConfig();
        this.apiClient = new ApiClient(config);
        this.uiExecutor = new UiExecutor();
        this.authService = new AuthService(apiClient);
        this.productService = new ProductService(apiClient);
        this.adminService = new AdminService(apiClient);
        this.lookupService = new LookupService(apiClient);
        this.favoriteService = new FavoriteService(apiClient);
        this.conversationService = new ConversationService(apiClient);
        this.ratingService = new RatingService(apiClient);
    }

    public Object createController(Class<?> controllerClass) {
        if (controllerClass == LoginController.class) {
            return new LoginController(authService, uiExecutor);
        }
        if (controllerClass == RegisterController.class) {
            return new RegisterController(authService, uiExecutor);
        }
        if (controllerClass == MainController.class) {
            return new MainController(authService);
        }
        if (controllerClass == ProductListController.class) {
            return new ProductListController(productService, lookupService, uiExecutor);
        }
        if (controllerClass == ProductFormController.class) {
            return new ProductFormController(productService, lookupService, uiExecutor);
        }
        if (controllerClass == ProductDetailController.class) {
            return new ProductDetailController(productService, favoriteService,
                    conversationService, ratingService, adminService, uiExecutor);
        }
        if (controllerClass == FavoritesController.class) {
            return new FavoritesController(favoriteService, uiExecutor);
        }
        if (controllerClass == ConversationsController.class) {
            return new ConversationsController(conversationService, uiExecutor);
        }
        if (controllerClass == AdminDashboardController.class) {
            return new AdminDashboardController(adminService, uiExecutor);
        }
        if (controllerClass == AdminPendingController.class) {
            return new AdminPendingController(adminService, uiExecutor);
        }
        if (controllerClass == AdminUsersController.class) {
            return new AdminUsersController(adminService, uiExecutor);
        }
        if (controllerClass == AdminLookupsController.class) {
            return new AdminLookupsController(lookupService, uiExecutor);
        }
        try {
            return controllerClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("No controller factory mapping for " + controllerClass.getName(), exception);
        }
    }

    public String getBackendBaseUrl() {
        return config.getBaseUrl();
    }

    @Override
    public void close() {
        uiExecutor.close();
    }
}
