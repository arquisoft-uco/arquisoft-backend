package com.arquisoft.usuarios.application.usuario.command.secondaryport.entity;

import java.util.UUID;

public record UsuarioEntity(UUID id, String email, String rol) {
}
