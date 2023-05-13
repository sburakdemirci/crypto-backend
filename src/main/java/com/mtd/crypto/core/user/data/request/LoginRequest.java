package com.mtd.crypto.core.user.data.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email property cannot be empty")
    @Email(message = "email is not valid")
    private String email;
    @NotBlank
    private String password;

}
