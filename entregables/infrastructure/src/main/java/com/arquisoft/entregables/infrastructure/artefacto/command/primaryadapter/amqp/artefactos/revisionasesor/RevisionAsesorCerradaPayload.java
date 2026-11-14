package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.revisionasesor;

import java.time.Instant;

public record RevisionAsesorCerradaPayload(
        String idEvento,
        Instant ocurridoEn,
        String documento,
        Integer numeroVersion,
        String proyectoGrado,
        String artefacto,
        String versionArtefacto,
        String uriArchivo,
        Instant fechaVersion,
        String versionRepositorioArtefacto,
        String repositorio,
        String nombreRepositorio
) {
}
