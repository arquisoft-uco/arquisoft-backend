package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.UUID;

public record EstadoProyectoGradoResponseDTO(
        UUID id,
        String estadoProyecto,
        String estadoProyectoNombre,
        Instant fechaActualizacion
) {}
