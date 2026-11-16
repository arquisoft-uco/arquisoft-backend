package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DocumentacionResponseDTO(
        UUID id,
        String tituloProyecto,
        Instant fechaPublicacion,
        EntregableProyectoGradoResponseDTO entregableProyectoGrado,
        BibliotecarioResponseDTO bibliotecario,
        EstadoDocumentacionResponseDTO estadoDocumentacion,
        List<AutorResponseDTO> autores
) {

    public record EntregableProyectoGradoResponseDTO(
            UUID id, UUID proyectoGrado, int versionEntregable, String estadoEntregable) {
    }

    public record BibliotecarioResponseDTO(UUID id, String nombre) {
    }

    public record EstadoDocumentacionResponseDTO(String id, String nombre) {
    }

    public record AutorResponseDTO(UUID id, String identificador, String nombre) {
    }
}
