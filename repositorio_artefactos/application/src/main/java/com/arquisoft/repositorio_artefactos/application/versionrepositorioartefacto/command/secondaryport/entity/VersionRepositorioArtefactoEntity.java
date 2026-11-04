package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record VersionRepositorioArtefactoEntity(
        UUID id,
        String nombreDocumento,
        int numeroVersion,
        Instant fechaCreacion,
        UUID repositorioArtefacto,
        String uriArchivo,
        String tipoContenido,
        long tamanoBytes,
        String nombreArchivo,
        String hashSha256
) {
}
