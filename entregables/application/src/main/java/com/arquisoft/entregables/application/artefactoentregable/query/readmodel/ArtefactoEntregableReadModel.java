package com.arquisoft.entregables.application.artefactoentregable.query.readmodel;

import java.util.UUID;

public record ArtefactoEntregableReadModel(
        UUID id,
        UUID entregableProyectoGrado,
        UUID versionArtefacto,
        String uriArchivo,
        UUID artefacto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio
) {
}
