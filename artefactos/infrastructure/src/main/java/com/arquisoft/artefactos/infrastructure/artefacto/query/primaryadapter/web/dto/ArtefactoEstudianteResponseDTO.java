package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtefactoEstudianteResponseDTO(
        UUID id,
        UUID proyectoGrado,
        String nombreProyecto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio,
        UUID asesor,
        String nombreAsesor
) {
}
