package com.arquisoft.artefactos.application.artefacto.query.readmodel;

import java.util.UUID;

public record ArtefactoEstudianteReadModel(
        UUID id,
        UUID proyectoGrado,
        String nombreProyecto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        int versionDocumento,
        String nombreRepositorio,
        UUID asesor,
        String nombreAsesor
) {
}
