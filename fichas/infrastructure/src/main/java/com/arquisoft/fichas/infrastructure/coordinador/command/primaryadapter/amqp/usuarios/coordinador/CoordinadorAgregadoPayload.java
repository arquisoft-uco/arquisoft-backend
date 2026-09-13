package com.arquisoft.fichas.infrastructure.coordinador.command.primaryadapter.amqp.usuarios.coordinador;

import java.time.Instant;

public record CoordinadorAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
