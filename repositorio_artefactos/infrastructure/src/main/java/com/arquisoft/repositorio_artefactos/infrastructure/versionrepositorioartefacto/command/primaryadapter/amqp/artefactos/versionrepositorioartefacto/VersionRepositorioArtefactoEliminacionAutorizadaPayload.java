package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import java.time.Instant;

public record VersionRepositorioArtefactoEliminacionAutorizadaPayload(
        String idEvento,
        Instant ocurridoEn,
        String versionRepositorioArtefacto,
        String nombreDocumento,
        Integer versionDocumento,
        String repositorioArtefacto
) {
}
