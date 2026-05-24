package com.arquisoft.seguridad.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequestDTO {

    @NotBlank(message = "El refresh token es requerido")
    private String refreshToken;
}
