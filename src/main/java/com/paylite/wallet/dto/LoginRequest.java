package com.paylite.wallet.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email , @NotBlank String password) {
}
