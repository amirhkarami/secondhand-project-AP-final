package ir.ac.aut.secondhand.frontend;

import ir.ac.aut.secondhand.frontend.context.AppContext;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

public final class MainApp extends Application {
    private AppContext appContext;

    @Override
    public void start(Stage primaryStage) {
        appContext = new AppContext();
        ViewManager.initialize(primaryStage, appContext);
        ViewManager.showMain();
        //ViewManager.showLogin();
    }

    @Override
    public void stop() {
        if (appContext != null) {
            appContext.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
