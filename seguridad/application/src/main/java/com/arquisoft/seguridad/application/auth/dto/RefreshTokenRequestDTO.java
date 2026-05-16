package com.arquisoft.seguridad.application.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de refresh token.
 * Contiene el refresh token necesario para obtener un nuevo access token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDTO {

    @NotBlank(message = "El refresh token es requerido")
    private String refreshToken;
}
