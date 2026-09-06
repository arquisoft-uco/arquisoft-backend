package com.arquisoft.solicitudes.application.usuario.command.secondaryport.entity;

import java.util.UUID;

public record UsuarioEntity(UUID id, String identificador, String nombre, String email) {
}
