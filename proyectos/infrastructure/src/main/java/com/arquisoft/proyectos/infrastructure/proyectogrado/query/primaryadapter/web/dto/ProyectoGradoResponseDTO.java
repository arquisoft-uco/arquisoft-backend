package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto;

import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.query.primaryadapter.web.dto.EstadoProyectoGradoResponseDTO;

import java.util.UUID;

public record ProyectoGradoResponseDTO(
        UUID id,
        UUID fichaPerfil,
        String tituloProyecto,
        EstadoProyectoGradoResponseDTO estado
) {}
