package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.estudiante;

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
