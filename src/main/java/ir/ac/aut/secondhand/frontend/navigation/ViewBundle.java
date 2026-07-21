package ir.ac.aut.secondhand.frontend.navigation;

import javafx.scene.Parent;

public final class ViewBundle<T> {
    private final Parent root;
    private final T controller;

    public ViewBundle(Parent root, T controller) {
        this.root = root;
        this.controller = controller;
    }

    public Parent getRoot() {
        return root;
    }

    public T getController() {
        return controller;
    }
}
