package com.diner.backend.security.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserInfoResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private List<String> roles;

    public UserInfoResponse(
                            @NotBlank @Size(max = 20) Long id,
                            String username,
                            String phone,
                            String email,
                            List<String> roles) {
        this.phone = phone;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}

