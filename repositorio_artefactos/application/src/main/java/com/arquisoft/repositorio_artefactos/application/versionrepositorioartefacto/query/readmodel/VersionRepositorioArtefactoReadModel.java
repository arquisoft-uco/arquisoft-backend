package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record VersionRepositorioArtefactoReadModel(
        UUID id,
        String nombreDocumento,
        int numeroVersion,
        Instant fechaCreacion,
        String tipoContenido,
        long tamanoBytes,
        String nombreArchivo,
        String hashSha256
) {
}
