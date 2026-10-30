package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VersionArtefactoResponseDTO(
        UUID id,
        UUID artefacto,
        int version,
        Instant fechaCreacion,
        String uriArchivo,
        String nombreProyecto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio,
        String nombreAsesor
) {
}
