package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.dto;

import com.arquisoft.proyectos.infrastructure.estadoproyecto.query.primaryadapter.web.dto.EstadoProyectoResponseDTO;

import java.util.UUID;

public record ProyectoGradoResponseDTO(
        UUID id,
        UUID fichaPerfil,
        String tituloProyecto,
        EstadoProyectoResponseDTO estado
) {}
