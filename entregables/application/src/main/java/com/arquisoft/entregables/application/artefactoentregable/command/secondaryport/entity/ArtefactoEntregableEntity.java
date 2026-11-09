package com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.entity;

import java.util.UUID;

public record ArtefactoEntregableEntity(
        UUID id,
        UUID versionArtefacto,
        UUID entregableProyectoGrado,
        String uriArchivo,
        UUID artefacto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio
) {
}
