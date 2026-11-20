package com.paylite.wallet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignupRequest( @NotBlank @Size(max = 100) String name,
                             @NotBlank @Email @Size(max = 255) String email,
                             @NotBlank @Pattern(regexp = "^[0-9]{10}$" , message = "must be exactly 10 digits") String phone,
                             @NotBlank @Size(min = 8 , max = 72) String password
                             ) {
}
