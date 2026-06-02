package com.arquisoft.seguridad.application.auth.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
