package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(

        @NotBlank(message = "El refresh token es requerido")
        String refreshToken) {
}
