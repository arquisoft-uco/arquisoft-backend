package com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity;

import java.util.UUID;

public record ArchivoDocumentacionAccesoEntity(
        UUID documentacion,
        String estado,
        String claveObjeto,
        String nombreArchivo
) {}
