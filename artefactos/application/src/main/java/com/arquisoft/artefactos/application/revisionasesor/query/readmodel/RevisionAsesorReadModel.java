package com.arquisoft.artefactos.application.revisionasesor.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record RevisionAsesorReadModel(
        UUID id,
        UUID versionArtefacto,
        UUID artefacto,
        int version,
        String estadoRevisionAsesor,
        String estadoRevisionAsesorNombre,
        Instant fechaCreacion,
        String nombreProyecto,
        String nombreDocumento,
        int versionDocumento,
        int observacionesAbiertas,
        int totalObservaciones
) {
}
