package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutResponseDTO {

    @Builder.Default
    private String mensaje = "Sesion cerrada exitosamente. Por favor, elimina el token almacenado.";
}
