package com.arquisoft.seguridad.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para validación de token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationResponseDTO {
    private boolean valid;
    private String keycloakUserId;
    private String email;
    private String message;
}
