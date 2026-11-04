package com.arquisoft.proyectos.application.estadoproyectogrado.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record EstadoProyectoGradoReadModel(
        UUID id,
        String estadoProyecto,
        String estadoProyectoNombre,
        Instant fechaActualizacion
) {}
