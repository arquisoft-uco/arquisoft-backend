package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.dto;

import java.util.UUID;

public record ArtefactoEntregableResponseDTO(
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
