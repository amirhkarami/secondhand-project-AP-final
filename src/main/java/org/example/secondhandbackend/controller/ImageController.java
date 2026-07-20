package org.example.secondhandbackend.controller;//import jakarta.annotation.Resource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/images")
public class ImageController {


    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String filename){

        try{

            String uploadDir = "uploads/";
            Path filePath =
                    Path.of(uploadDir).resolve(filename);


            Resource resource =
                    new UrlResource(filePath.toUri());


            if(!resource.exists()){
                return ResponseEntity.notFound().build();
            }


            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);


        }catch(Exception e){

            return ResponseEntity.notFound().build();

        }
    }
}