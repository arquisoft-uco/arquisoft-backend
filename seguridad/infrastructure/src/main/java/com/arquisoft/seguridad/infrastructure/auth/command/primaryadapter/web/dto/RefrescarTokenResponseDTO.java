package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefrescarTokenResponseDTO {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
    private String scope;
}
