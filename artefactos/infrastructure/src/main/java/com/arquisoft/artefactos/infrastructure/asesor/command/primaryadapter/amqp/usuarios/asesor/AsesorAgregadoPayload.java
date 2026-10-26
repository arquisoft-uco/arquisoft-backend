package com.arquisoft.artefactos.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import java.time.Instant;

public record AsesorAgregadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
