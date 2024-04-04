package com.libary.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ImageUpload {
    private final String UPLOAD_FOLDER = "C:\\Users\\jackn\\OneDrive\\Documents\\EcommerceProject\\Admin\\src\\main\\resources\\static\\img\\UploadImages";
    public boolean uploadFile(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), Paths.get(UPLOAD_FOLDER + File.separator + file.getOriginalFilename()) , StandardCopyOption.REPLACE_EXISTING);
           return true;
        } catch (Exception e) {
            e.printStackTrace();
        System.out.println("failed to upload file");
        }
        return false;
    }
    public boolean checkExist(MultipartFile multipartFile) {
        boolean exists = false;
        try {
            File file = new File(UPLOAD_FOLDER +"\\" + multipartFile.getOriginalFilename());
            return file.exists();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
