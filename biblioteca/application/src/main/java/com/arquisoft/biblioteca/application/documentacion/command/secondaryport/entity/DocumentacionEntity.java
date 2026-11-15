package com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record DocumentacionEntity(
        UUID id,
        String url,
        Instant fechaPublicacion,
        UUID entregableProyectoGrado,
        UUID bibliotecario,
        String estadoDocumentacion
) {}
