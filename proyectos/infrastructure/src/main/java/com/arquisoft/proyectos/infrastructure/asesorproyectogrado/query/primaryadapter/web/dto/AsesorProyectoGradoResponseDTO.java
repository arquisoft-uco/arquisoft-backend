package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.UUID;

public record AsesorProyectoGradoResponseDTO(
        UUID id,
        UUID proyectoGradoId,
        UUID asesorId,
        String identificador,
        String nombre,
        String email,
        String estadoAsesor,
        String estadoAsesorNombre,
        Instant fechaAsignado,
        boolean vigente
) {}
