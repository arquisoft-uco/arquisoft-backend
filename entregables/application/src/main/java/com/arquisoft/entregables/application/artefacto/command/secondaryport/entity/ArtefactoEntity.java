package com.arquisoft.entregables.application.artefacto.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoEntity(
        UUID id,
        UUID proyectoGrado,
        UUID versionArtefacto,
        int numeroVersion,
        Instant fechaVersion,
        String claveObjeto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio,
        Instant omitidoEn,
        Instant omisionOcurridaEn
) {
}
