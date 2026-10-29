package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EntregableProyectoGradoResponseDTO(
        UUID id,
        int versionEntregable,
        ProyectoGradoResponseDTO proyectoGrado,
        AsesorResponseDTO asesor,
        EstadoEntregableResponseDTO estadoEntregable
) {

    public record ProyectoGradoResponseDTO(UUID id, String nombre) {
    }

    public record AsesorResponseDTO(UUID id, String nombre, String email) {
    }

    public record EstadoEntregableResponseDTO(String id, String nombre) {
    }
}
