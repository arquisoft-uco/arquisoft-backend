package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import java.time.Instant;

public record AsesorProyectoGradoAsignadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String asesorId
) {
}
