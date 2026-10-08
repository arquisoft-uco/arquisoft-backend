package com.arquisoft.usuarios.application.usuario.command.secondaryport.entity;

import java.util.UUID;

public record ModificacionIdentidadEntity(
        UUID usuario,
        String email,
        String nombres,
        String apellidos) {
}
