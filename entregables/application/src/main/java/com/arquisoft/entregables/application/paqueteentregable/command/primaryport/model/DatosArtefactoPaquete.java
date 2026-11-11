package com.arquisoft.entregables.application.paqueteentregable.command.primaryport.model;

public record DatosArtefactoPaquete(
        String artefacto,
        String versionArtefacto,
        Integer numeroVersion,
        String versionRepositorioArtefacto,
        String nombreDocumento,
        String repositorio,
        String nombreRepositorio,
        String rutaEnPaquete
) {}
