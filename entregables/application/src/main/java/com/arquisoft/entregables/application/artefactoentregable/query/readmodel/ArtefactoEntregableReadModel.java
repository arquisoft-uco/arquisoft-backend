package com.arquisoft.entregables.application.artefactoentregable.query.readmodel;

import java.util.UUID;

public record ArtefactoEntregableReadModel(
        UUID id,
        UUID entregableProyectoGrado,
        UUID versionArtefacto,
        int numeroVersion,
        String uriArchivo,
        UUID artefacto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio
) {
}
