package com.diner.backend.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryImageService {

    public Map uploadImage(MultipartFile file);
}
