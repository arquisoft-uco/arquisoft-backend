package com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel;

import java.util.UUID;

public record ParticipacionArtefactoReadModel(
        UUID id,
        UUID proyectoGrado,
        String nombreProyecto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio
) {
}
