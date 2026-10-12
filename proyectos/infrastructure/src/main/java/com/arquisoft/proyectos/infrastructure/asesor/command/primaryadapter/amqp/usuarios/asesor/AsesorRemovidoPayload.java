package com.arquisoft.proyectos.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import java.time.Instant;

public record AsesorRemovidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String usuario,
        String identificador,
        String nombre,
        String email
) {
}
