package com.arquisoft.usuarios.application.usuario.query.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record UsuarioAccesoEntity(
        UUID id,
        String identificador,
        String nombre,
        String email,
        String contacto,
        String estado,
        Instant eliminadoEn) {
}
