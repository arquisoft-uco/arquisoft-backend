package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import java.time.Instant;
import java.util.List;

public record EstudiantesProyectoGradoAsignadosPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        List<EstudianteAsignado> estudiantes
) {

    public record EstudianteAsignado(String id) {
    }
}
