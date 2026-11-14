package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.UUID;

public record DocumentacionResponseDTO(
        UUID id,
        String url,
        Instant fechaPublicacion,
        EntregableProyectoGradoResponseDTO entregableProyectoGrado,
        BibliotecarioResponseDTO bibliotecario,
        EstadoDocumentacionResponseDTO estadoDocumentacion
) {

    public record EntregableProyectoGradoResponseDTO(
            UUID id, UUID proyectoGrado, int versionEntregable, String estadoEntregable) {
    }

    public record BibliotecarioResponseDTO(UUID id, String nombre) {
    }

    public record EstadoDocumentacionResponseDTO(String id, String nombre) {
    }
}
