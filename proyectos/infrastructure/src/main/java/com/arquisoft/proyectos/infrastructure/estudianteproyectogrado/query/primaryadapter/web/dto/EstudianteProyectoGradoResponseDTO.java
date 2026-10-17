package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EstudianteProyectoGradoResponseDTO(
        UUID id,
        UUID proyectoGradoId,
        UUID estudianteId,
        String identificador,
        String nombre,
        String email,
        boolean vigente
) {}
