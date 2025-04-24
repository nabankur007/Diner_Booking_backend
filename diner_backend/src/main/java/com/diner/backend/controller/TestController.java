package com.diner.backend.controller;

import com.diner.backend.security.jwt.JwtUtils;
import com.diner.backend.service.serviceimpl.CloudinaryImageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    CloudinaryImageServiceImpl cloudinaryImageServiceimpl;

    @GetMapping("/getusername/{token}")
    public String test(@PathVariable String token) {
//        System.out.println("dadd");
        return jwtUtils.getUserNameFromJwtToken(token);
    }

    @PostMapping("/upload")
    public ResponseEntity<Map> uploadImage(@RequestParam("image")MultipartFile file){
       Map data =this.cloudinaryImageServiceimpl.uploadImage(file);
       return new ResponseEntity<>(data, HttpStatus.OK);
    }

}
