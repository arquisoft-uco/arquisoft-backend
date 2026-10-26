package com.arquisoft.artefactos.application.versionartefacto.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record VersionArtefactoReadModel(
        UUID id,
        UUID artefacto,
        int version,
        Instant fechaCreacion,
        String uriArchivo
) {
}
