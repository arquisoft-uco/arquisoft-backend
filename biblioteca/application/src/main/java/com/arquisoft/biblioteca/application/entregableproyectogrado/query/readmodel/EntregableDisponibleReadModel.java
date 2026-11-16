package com.arquisoft.biblioteca.application.entregableproyectogrado.query.readmodel;

import java.util.List;
import java.util.UUID;

public record EntregableDisponibleReadModel(
        UUID id,
        UUID proyectoGrado,
        String tituloProyecto,
        int versionEntregable,
        String nombreArchivo,
        List<AutorResumen> autores
) {

    public record AutorResumen(UUID id, String identificador, String nombre) {}
}
