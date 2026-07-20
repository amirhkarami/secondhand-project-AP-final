package org.example.secondhandbackend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
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
}