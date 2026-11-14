package com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.entity;

import java.util.UUID;

public record ArchivoVersionArtefactoEntity(
        UUID asesor, String uriArchivo, int numeroVersion, String nombreDocumento, String nombreProyecto,
        boolean proyectoFinalizado) {}
