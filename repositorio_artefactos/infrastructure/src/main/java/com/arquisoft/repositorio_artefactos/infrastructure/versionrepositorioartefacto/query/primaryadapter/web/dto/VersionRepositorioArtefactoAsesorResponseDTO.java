package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VersionRepositorioArtefactoAsesorResponseDTO(
        UUID id,
        String nombreDocumento,
        int numeroVersion,
        Instant fechaCreacion,
        String tipoContenido,
        long tamanoBytes,
        String nombreArchivo,
        String hashSha256,
        RepositorioDeVersionResponseDTO repositorioArtefacto
) {
}
