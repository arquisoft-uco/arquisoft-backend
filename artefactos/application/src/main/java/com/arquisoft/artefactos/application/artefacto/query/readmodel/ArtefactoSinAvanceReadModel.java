package com.arquisoft.artefactos.application.artefacto.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoSinAvanceReadModel(
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
