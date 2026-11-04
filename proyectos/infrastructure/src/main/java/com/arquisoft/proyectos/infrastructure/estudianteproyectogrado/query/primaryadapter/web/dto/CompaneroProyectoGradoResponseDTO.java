package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.primaryadapter.web.dto;

import java.util.UUID;

public record CompaneroProyectoGradoResponseDTO(UUID estudianteId, String identificador, String nombre, String email) {}
