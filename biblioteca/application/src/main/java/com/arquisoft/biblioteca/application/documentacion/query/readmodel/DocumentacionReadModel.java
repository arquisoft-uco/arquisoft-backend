package com.arquisoft.biblioteca.application.documentacion.query.readmodel;

import com.arquisoft.shared.util.UtilColeccion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DocumentacionReadModel(
        UUID id,
        String tituloProyecto,
        Instant fechaPublicacion,
        EntregableProyectoGradoResumen entregableProyectoGrado,
        BibliotecarioResumen bibliotecario,
        EstadoDocumentacionResumen estadoDocumentacion,
        List<AutorResumen> autores
) {

    public DocumentacionReadModel {
        autores = UtilColeccion.aplicarPorDefecto(autores);
    }

    public record EntregableProyectoGradoResumen(
            UUID id, UUID proyectoGrado, int versionEntregable, String estadoEntregable) {
    }

    public record BibliotecarioResumen(UUID id, String nombre) {
    }

    public record EstadoDocumentacionResumen(String id, String nombre) {
    }

    public record AutorResumen(UUID id, String identificador, String nombre) {
    }
}
