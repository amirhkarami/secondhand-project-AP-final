package ir.ac.aut.secondhand.frontend.controller;

import ir.ac.aut.secondhand.frontend.dto.CategoryDto;
import ir.ac.aut.secondhand.frontend.dto.CityDto;
import ir.ac.aut.secondhand.frontend.dto.ProductDetailDto;
import ir.ac.aut.secondhand.frontend.dto.ProductImageDto;
import ir.ac.aut.secondhand.frontend.navigation.ViewManager;
import ir.ac.aut.secondhand.frontend.service.LookupService;
import ir.ac.aut.secondhand.frontend.service.ProductService;
import ir.ac.aut.secondhand.frontend.util.Alerts;
import ir.ac.aut.secondhand.frontend.util.UiExecutor;
import ir.ac.aut.secondhand.frontend.util.ValidationUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
;

public final class ProductFormController {
    private final ProductService productService;
    private final LookupService lookupService;
    private final UiExecutor uiExecutor;
    private final List<Path> selectedImages = new ArrayList<>();

   // private ProductDto editingProduct;
   private ProductDetailDto editingProduct;

    @FXML
    private Label pageTitle;
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionArea;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<CategoryDto> categoryCombo;
    @FXML
    private ComboBox<CityDto> cityCombo;
    @FXML
    private ListView<String> imageList;
    @FXML
    private Button submitButton;
    @FXML
    private Label stateLabel;
    @FXML
    private FlowPane imageContainer;

    public ProductFormController(ProductService productService, LookupService lookupService,
                                 UiExecutor uiExecutor) {
        this.productService = productService;
        this.lookupService = lookupService;
        this.uiExecutor = uiExecutor;
    }
    @FXML
    private void initialize() {
        loadLookups();
    }

//public void setEditingProduct(ProductDetailDto product) {
//    this.editingProduct = product;
//    if(product != null){
//        titleField.setText(product.getTitle());
//        descriptionArea.setText(product.getDescription());
//        priceField.setText(String.valueOf(product.getPrice()));
//        loadExistingImages();
//    }
//}
public void setEditingProduct(ProductDetailDto product) {
        this.editingProduct = product;
        if(product == null){

            pageTitle.setText("Create a new advertisement");
            submitButton.setText("Submit for review");

            return;
        }


        pageTitle.setText("Edit advertisement");
        submitButton.setText("Save changes");


        titleField.setText(product.getTitle());

        descriptionArea.setText(
                product.getDescription()
        );

        priceField.setText(
                String.valueOf(product.getPrice())
        );


        loadExistingImages();
    }


    @FXML
    private void onChooseImages() {


        FileChooser chooser = new FileChooser();

        chooser.setTitle("Select advertisement images");


        chooser.getExtensionFilters()
                .add(
                        new FileChooser.ExtensionFilter(
                                "Images",
                                "*.png",
                                "*.jpg",
                                "*.jpeg",
                                "*.webp"
                        )
                );


        List<File> files =
                chooser.showOpenMultipleDialog(
                        submitButton.getScene().getWindow()
                );


        if(files != null){


            files.stream()
                    .map(File::toPath)
                    .forEach(selectedImages::add);


            imageList.getItems()
                    .addAll(
                            files.stream()
                                    .map(File::getName)
                                    .toList()
                    );
        }
    }



    @FXML
    private void onClearImages() {
        selectedImages.clear();
        imageList.getItems().clear();
    }

    @FXML
    private void onSubmit() {
        try {
            ValidationUtils.requireText(titleField.getText(), "Title");
            long price = ValidationUtils.parsePositiveLong(priceField.getText(), "Price");
            CategoryDto category = categoryCombo.getValue();
            CityDto city = cityCombo.getValue();
            if (category == null) {
                throw new IllegalArgumentException("Please select a category");
            }
            if (city == null) {
                throw new IllegalArgumentException("Please select a city");
            }
            setBusy(true, "Sending advertisement to backend...");
            if (editingProduct == null) {
                uiExecutor.runVoid(
                        () -> productService.create(titleField.getText().trim(),
                                descriptionArea.getText(), price, category.getId(), city.getId(), selectedImages),
                        () -> {
                            setBusy(false, "");
                            Alerts.info("Advertisement created",
                                    "Your advertisement is waiting for administrator approval.");
                            ViewManager.showProducts();
                            },
                        error -> {
                            setBusy(false, "");
                            Alerts.error("Create advertisement", error);
                        });
            } else {
                uiExecutor.runVoid(
                        () -> productService.update(editingProduct.getId(), titleField.getText().trim(),
                                descriptionArea.getText(), price, category.getId(), city.getId(), selectedImages),
                        () -> {
                            setBusy(false, "");
                            Alerts.info("Advertisement updated",
                                    "Changes were saved and the advertisement is pending review again.");
                            ViewManager.showProductDetail(editingProduct.getId());
                        },
                        error -> {
                            setBusy(false, "");
                            Alerts.error("Update advertisement", error);
                        });
            }
        } catch (IllegalArgumentException exception) {
            Alerts.error("Invalid advertisement", exception.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        if (editingProduct != null && editingProduct.getId() != 0) {
            ViewManager.showProductDetail(editingProduct.getId());
        } else {
            ViewManager.showProducts();
        }
    }

    private void loadLookups() {
        uiExecutor.run(lookupService::getCategories,
                categories -> {
                    categoryCombo.getItems().setAll(categories);
                    selectCategoryAndCity();
                },
                error -> Alerts.error("Categories", error));
        uiExecutor.run(lookupService::getCities,
                cities -> {
                    cityCombo.getItems().setAll(cities);
                    selectCategoryAndCity();
                },
                error -> Alerts.error("Cities", error));
    }

    private void selectCategoryAndCity() {
        if (editingProduct == null) {
            return;
        }
        Long categoryId = editingProduct.getCategoryId();
        if (categoryId != null) {
            categoryCombo.getItems().stream()
                    .filter(item -> categoryId.equals(item.getId()))
                    .findFirst().ifPresent(categoryCombo::setValue);
        }
        Integer cityId = editingProduct.getCityId();
        if (cityId != null) {
            cityCombo.getItems().stream()
                    .filter(item -> cityId.equals(item.getId()))
                    .findFirst().ifPresent(cityCombo::setValue);
        }
    }
    private void setBusy(boolean busy, String message) {
        submitButton.setDisable(busy);
        stateLabel.setText(message);
    }
    private void loadExistingImages(){
        imageContainer.getChildren().clear();
        if(editingProduct.getImages() == null)
            return;
        for(ProductImageDto image : editingProduct.getImages()){
            ImageView imageView = new ImageView(new Image(image.getImagePath(), true));
            imageView.setFitWidth(120);
            imageView.setFitHeight(90);
            Button deleteButton = new Button("X");
            deleteButton.setOnAction(e -> deleteExistingImage(image));
            VBox box = new VBox(5, imageView, deleteButton);
            imageContainer.getChildren().add(box);
        }
    }
    private void deleteExistingImage(ProductImageDto image){
        uiExecutor.runVoid(() -> productService.deleteImage(editingProduct.getId(), image.getId()), () -> {
                    editingProduct.getImages().remove(image);
                    loadExistingImages();
                    Alerts.info("Image deleted", "Image removed successfully");
                    }, error -> Alerts.error("Delete image", error));
    }
}
