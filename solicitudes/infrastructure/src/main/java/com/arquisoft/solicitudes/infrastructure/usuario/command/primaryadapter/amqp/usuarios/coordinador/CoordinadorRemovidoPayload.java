package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.coordinador;

import java.time.Instant;

public record CoordinadorRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
