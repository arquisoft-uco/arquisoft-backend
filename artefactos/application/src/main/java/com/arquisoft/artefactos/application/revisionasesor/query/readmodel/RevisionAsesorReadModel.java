package com.arquisoft.artefactos.application.revisionasesor.query.readmodel;

import java.util.UUID;

public record RevisionAsesorReadModel(
        UUID id,
        UUID versionArtefacto,
        UUID artefacto,
        int version,
        String estadoRevisionAsesor,
        String estadoRevisionAsesorNombre
) {
}
