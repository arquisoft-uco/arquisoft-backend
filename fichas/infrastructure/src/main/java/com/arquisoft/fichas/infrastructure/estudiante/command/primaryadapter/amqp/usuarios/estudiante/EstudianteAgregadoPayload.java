package com.arquisoft.fichas.infrastructure.estudiante.command.primaryadapter.amqp.usuarios.estudiante;

import java.time.Instant;

public record EstudianteAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
