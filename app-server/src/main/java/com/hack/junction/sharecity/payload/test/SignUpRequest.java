package com.hack.junction.sharecity.payload.test;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SignUpRequest {
    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    @NotBlank
    private String role;
}
