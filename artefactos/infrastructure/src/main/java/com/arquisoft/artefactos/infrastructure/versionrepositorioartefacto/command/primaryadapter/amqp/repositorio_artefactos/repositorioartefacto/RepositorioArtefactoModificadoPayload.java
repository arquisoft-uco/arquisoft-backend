package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.repositorioartefacto;

import java.time.Instant;

public record RepositorioArtefactoModificadoPayload(
        String idEvento,
        Instant ocurridoEn,
        String repositorioArtefacto,
        String nombreRepositorio
) {
}
