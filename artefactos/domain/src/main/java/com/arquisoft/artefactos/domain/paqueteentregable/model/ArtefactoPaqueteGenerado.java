package com.arquisoft.artefactos.domain.paqueteentregable.model;

import java.util.UUID;

public record ArtefactoPaqueteGenerado(
        UUID artefacto,
        int numeroVersion,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio,
        String rutaEnPaquete
) {}
