package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity;

import java.util.UUID;

public record VersionRepositorioArtefactoEntity(
        UUID id,
        String nombreDocumento,
        int versionDocumento,
        UUID repositorioArtefacto,
        String nombreRepositorio,
        String nombreArchivo,
        String tipoContenido,
        long tamanoBytes
) {
}
