package com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record AsesorProyectoGradoReadModel(
        UUID id,
        UUID proyectoGradoId,
        UUID asesorId,
        String identificador,
        String nombre,
        String email,
        String estadoAsesor,
        String estadoAsesorNombre,
        Instant fechaAsignado,
        boolean vigente,
        boolean actual
) {}
