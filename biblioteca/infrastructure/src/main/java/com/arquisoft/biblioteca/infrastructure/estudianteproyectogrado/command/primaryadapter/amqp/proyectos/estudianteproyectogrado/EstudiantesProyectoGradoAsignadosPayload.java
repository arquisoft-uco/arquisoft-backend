package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import java.time.Instant;
import java.util.List;

public record EstudiantesProyectoGradoAsignadosPayload(
        String idEvento,
        Instant ocurridoEn,
        String proyectoGradoId,
        String tituloProyecto,
        List<EstudiantePayload> estudiantes
) {
    public record EstudiantePayload(String id) {
    }
}
