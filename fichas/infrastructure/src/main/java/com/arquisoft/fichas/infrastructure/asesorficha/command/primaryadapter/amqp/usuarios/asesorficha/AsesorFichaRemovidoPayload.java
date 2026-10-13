package com.arquisoft.fichas.infrastructure.asesorficha.command.primaryadapter.amqp.usuarios.asesorficha;

import java.time.Instant;

public record AsesorFichaRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
