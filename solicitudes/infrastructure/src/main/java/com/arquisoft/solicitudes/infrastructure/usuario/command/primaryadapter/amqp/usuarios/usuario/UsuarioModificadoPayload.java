package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.usuario;

import java.time.Instant;

public record UsuarioModificadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
