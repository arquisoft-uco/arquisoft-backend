package com.arquisoft.seguridad.application.auth.readmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReadModel para la respuesta de validacion de token (resultado de consulta).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationReadModel {
    private boolean valid;
    private String keycloakUserId;
    private String email;
    private String message;
}
