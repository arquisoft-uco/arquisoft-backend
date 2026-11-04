package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import java.time.Instant;

public record EstudianteProyectoGradoDestituidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String estudianteId
) {
}
