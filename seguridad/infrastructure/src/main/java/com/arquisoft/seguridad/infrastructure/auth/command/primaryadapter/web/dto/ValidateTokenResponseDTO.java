package com.arquisoft.seguridad.infrastructure.auth.command.primaryadapter.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateTokenResponseDTO {
    private boolean valido;
    private String identidadId;
    private String correo;
    private String mensaje;
}
