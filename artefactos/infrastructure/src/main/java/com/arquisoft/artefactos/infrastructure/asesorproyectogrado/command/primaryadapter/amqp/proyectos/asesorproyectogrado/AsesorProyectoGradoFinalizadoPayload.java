package com.arquisoft.artefactos.infrastructure.asesorproyectogrado.command.primaryadapter.amqp.proyectos.asesorproyectogrado;

import java.time.Instant;

public record AsesorProyectoGradoFinalizadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String asesorId
) {
}
