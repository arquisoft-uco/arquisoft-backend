package com.arquisoft.biblioteca.application.documentacion.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record DocumentacionReadModel(
        UUID id,
        String url,
        Instant fechaPublicacion,
        EntregableProyectoGradoResumen entregableProyectoGrado,
        BibliotecarioResumen bibliotecario,
        EstadoDocumentacionResumen estadoDocumentacion
) {

    public record EntregableProyectoGradoResumen(
            UUID id, UUID proyectoGrado, int versionEntregable, String estadoEntregable) {
    }

    public record BibliotecarioResumen(UUID id, String nombre) {
    }

    public record EstadoDocumentacionResumen(String id, String nombre) {
    }
}
