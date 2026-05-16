package com.arquisoft.seguridad.application.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de logout.
 * Confirma el cierre de sesion al cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutResponseDTO {

    @Builder.Default
    private String message = "Sesion cerrada exitosamente. Por favor, elimina el token almacenado.";
}
