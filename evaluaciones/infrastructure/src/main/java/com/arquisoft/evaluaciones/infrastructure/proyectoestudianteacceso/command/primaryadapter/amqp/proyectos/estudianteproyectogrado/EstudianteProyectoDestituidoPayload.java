package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import java.time.Instant;

public record EstudianteProyectoDestituidoPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoId,
        String estudianteId
) {
}
