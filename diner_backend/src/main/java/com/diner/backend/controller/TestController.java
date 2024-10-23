package com.diner.backend.controller;

import com.diner.backend.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    JwtUtils jwtUtils;

    @GetMapping("/{token}")
    public String test(@PathVariable String token) {
        System.out.println("dadd");
        return jwtUtils.getUserNameFromJwtToken(token);
    }
}
