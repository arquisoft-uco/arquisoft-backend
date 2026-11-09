package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

import java.util.UUID;

public record ArtefactoPaqueteCommand(
        UUID artefacto,
        UUID versionRepositorioArtefacto,
        String nombreDocumento,
        UUID repositorio,
        String nombreRepositorio,
        String rutaEnPaquete
) {}
