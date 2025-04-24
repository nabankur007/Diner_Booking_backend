package com.diner.backend.service.serviceimpl;

import com.cloudinary.Cloudinary;
import com.diner.backend.service.CloudinaryImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryImageServiceImpl implements CloudinaryImageService {

    @Autowired
    public Cloudinary cloudinary;

    @Override
    public Map uploadImage(MultipartFile file) {
        try {
            Map data = this.cloudinary.uploader().upload(file.getBytes(),Map.of());
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
