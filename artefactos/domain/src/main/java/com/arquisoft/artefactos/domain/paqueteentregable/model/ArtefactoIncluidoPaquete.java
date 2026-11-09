package com.arquisoft.artefactos.domain.paqueteentregable.model;

import java.time.Instant;
import java.util.UUID;

public record ArtefactoIncluidoPaquete(
        UUID artefacto,
        UUID versionArtefacto,
        String nombreDocumento,
        int numeroVersion,
        int ultimaVersion,
        Instant fechaVersion,
        String uriArchivo,
        UUID versionRepositorioArtefacto,
        UUID repositorio,
        String nombreRepositorio
) {}
