package org.example.secondhandbackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.example.secondhandbackend.exception.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    public CloudinaryService(Cloudinary cloudinary){
        this.cloudinary = cloudinary;
    }
    public String upload(MultipartFile file){
        try {
            var result = cloudinary.uploader()
                    .upload(
                            file.getBytes(),
                            ObjectUtils.emptyMap()
                    );
            return result.get("secure_url").toString();
        } catch (Exception e){
            throw new RuntimeException("Image upload failed");
        }
    }

    public void delete(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new ApiException("unable to delete image", 400);
        }
    }
    private String extractPublicId(String url) {
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }
}