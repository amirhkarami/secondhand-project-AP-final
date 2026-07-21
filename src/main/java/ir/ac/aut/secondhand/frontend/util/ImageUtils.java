package ir.ac.aut.secondhand.frontend.util;

import ir.ac.aut.secondhand.frontend.dto.ProductImageDto;
import javafx.scene.image.Image;

public final class ImageUtils {

    private ImageUtils() {
    }

    public static Image toImage(ProductImageDto imageDto) {

        if (imageDto == null) {
            return null;
        }

        try {
            String imagePath = imageDto.getImagePath();

            if (imagePath != null && !imagePath.isBlank()) {

                String imageUrl =  imagePath;

                return new Image(imageUrl, true);
            }

        } catch (Exception ignored) {
        }

        return null;
    }
}