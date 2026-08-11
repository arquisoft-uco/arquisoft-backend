package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

@Value
@Builder
public class CrearUsuarioResponseDTO {
    UUID id;
    String email;
    String rol;
}
