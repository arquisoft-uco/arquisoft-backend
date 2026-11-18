package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.dto;

import java.util.UUID;

public record AsesorActualProyectoGradoResponseDTO(
        UUID proyectoGradoId,
        String tituloProyecto,
        UUID asesorId,
        String identificador,
        String nombre,
        String email
) {}
