package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ArtefactoSinAvanceResponseDTO(
        UUID id,
        UUID proyectoGrado,
        String nombreProyecto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio,
        Instant fechaUltimaVersion,
        Long diasSinAvance,
        String estadoUltimaRevision
) {
}
