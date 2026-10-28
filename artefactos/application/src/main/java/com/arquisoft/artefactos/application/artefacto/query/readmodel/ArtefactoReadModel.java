package com.arquisoft.artefactos.application.artefacto.query.readmodel;

import java.util.UUID;

public record ArtefactoReadModel(
        UUID id,
        UUID proyectoGrado,
        String nombreProyecto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio
) {
}
