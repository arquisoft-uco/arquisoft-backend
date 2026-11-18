package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto;

import java.util.UUID;

public record CoordinadorProyectoGradoResponseDTO(
        UUID proyectoGradoId,
        String tituloProyecto,
        UUID coordinadorId,
        String identificador,
        String nombre,
        String email
) {}
