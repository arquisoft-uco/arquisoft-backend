package com.arquisoft.evaluaciones.infrastructure.jurado.command.primaryadapter.amqp.usuarios.jurado;

import java.time.Instant;

public record JuradoRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
