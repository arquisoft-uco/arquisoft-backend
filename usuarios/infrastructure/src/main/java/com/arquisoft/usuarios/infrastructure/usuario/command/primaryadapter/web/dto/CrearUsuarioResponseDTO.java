package com.arquisoft.usuarios.infrastructure.usuario.command.primaryadapter.web.dto;

import java.util.UUID;

public record CrearUsuarioResponseDTO(UUID id, String email, String rol) {
}
