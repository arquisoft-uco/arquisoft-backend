package com.arquisoft.usuarios.application.usuario.command.secondaryport.entity;

import java.util.UUID;

public record UsuarioEntity(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado) {
}
