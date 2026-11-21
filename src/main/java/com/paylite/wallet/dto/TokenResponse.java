package com.paylite.wallet.dto;

public record TokenResponse(String accessToken, String tokenType, long expiresInSeconds) {
}
