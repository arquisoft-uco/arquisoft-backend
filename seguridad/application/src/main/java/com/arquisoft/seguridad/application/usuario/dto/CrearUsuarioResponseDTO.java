package com.arquisoft.seguridad.application.usuario.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * DTO de respuesta tras crear un usuario exitosamente.
 */
@Value
@Builder
public class CrearUsuarioResponseDTO {
    UUID id;
    String email;
    String rol;
}
