package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.versionrepositorioartefacto;

import java.time.Instant;

public record VersionRepositorioArtefactoSubidaPayload(
        String idEvento,
        Instant ocurridoEn,
        String versionRepositorioArtefacto,
        String nombreDocumento,
        Integer versionDocumento,
        String repositorioArtefacto,
        String nombreRepositorio,
        String nombreArchivo,
        String tipoContenido,
        Long tamanoBytes
) {
}
