package org.example.secondhandbackend.controller;


import org.example.secondhandbackend.exception.ApiException;
import org.example.secondhandbackend.model.ProductImage;
import org.example.secondhandbackend.repository.ProductImageRepository;
import org.example.secondhandbackend.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/products")
public class ProductImageController {

    private final ProductImageRepository imageRepository;
    private final CloudinaryService cloudinaryService;


    public ProductImageController(
            ProductImageRepository imageRepository,
            CloudinaryService cloudinaryService
    ) {
        this.imageRepository = imageRepository;
        this.cloudinaryService = cloudinaryService;
    }
    @DeleteMapping("/{productId}/images/{imageId}")
    public ResponseEntity<String> deleteImage(@PathVariable int productId, @PathVariable Long imageId, Authentication authentication
    ){
        if(authentication == null)
            throw new ApiException("UNAUTHORIZED",401);
        ProductImage image = imageRepository.findById(imageId).orElseThrow(() -> new ApiException("image not found",40));
        // check image belongs to product
        if(image.getProduct().getId() != productId){
            throw new ApiException("image does not belong to this product", 400);
        }

        boolean owner = image.getProduct().getUser().getUsername().equals(authentication.getName());
        if(!owner){
            throw new ApiException("you cannot delete this image", 403);
        }

        cloudinaryService.delete(image.getImagePath());
        image.getProduct().getImages().remove(image);
        imageRepository.deleteById(imageId);
        return ResponseEntity.ok(
                "image deleted");
    }
}