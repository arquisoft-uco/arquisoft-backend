package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EstudianteArtefactoResponseDTO(
        UUID id,
        UUID artefactoId,
        UUID estudianteId,
        String identificador,
        String nombre,
        String email,
        boolean vigente
) {
}
