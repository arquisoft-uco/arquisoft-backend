package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import java.time.Instant;
import java.util.List;

public record EstudiantesProyectoGradoAsignadosPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        List<EstudiantePayload> estudiantes
) {
    public record EstudiantePayload(String id) {
    }
}
