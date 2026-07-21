package ir.ac.aut.secondhand.frontend.util;

import ir.ac.aut.secondhand.frontend.client.ApiException;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public final class Alerts {
    private Alerts() {
    }

    public static void info(String title, String message) {
        show(Alert.AlertType.INFORMATION, title, message);
    }

    public static void error(String title, Throwable throwable) {
        String message;
        if (throwable instanceof ApiException apiException) {
            message = apiException.getMessage();
            if (apiException.getStatusCode() > 0) {
                message += "\nStatus: " + apiException.getStatusCode();
            }
        } else {
            message = throwable == null || throwable.getMessage() == null
                    ? "Unexpected error"
                    : throwable.getMessage();
        }
        show(Alert.AlertType.ERROR, title, message);
    }

    public static void error(String title, String message) {
        show(Alert.AlertType.ERROR, title, message);
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private static void show(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
