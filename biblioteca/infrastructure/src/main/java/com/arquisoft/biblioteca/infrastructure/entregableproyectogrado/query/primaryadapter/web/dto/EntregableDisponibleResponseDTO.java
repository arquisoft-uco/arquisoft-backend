package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto;

import java.util.List;
import java.util.UUID;

public record EntregableDisponibleResponseDTO(
        UUID id,
        UUID proyectoGrado,
        String tituloProyecto,
        int versionEntregable,
        String nombreArchivo,
        List<AutorResponseDTO> autores
) {

    public record AutorResponseDTO(UUID id, String identificador, String nombre) {}
}
