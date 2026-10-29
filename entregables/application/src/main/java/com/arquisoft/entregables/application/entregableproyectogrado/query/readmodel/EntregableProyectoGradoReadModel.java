package com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel;

import java.util.UUID;

public record EntregableProyectoGradoReadModel(
        UUID id,
        int versionEntregable,
        ProyectoGradoResumen proyectoGrado,
        AsesorResumen asesor,
        EstadoEntregableResumen estadoEntregable
) {

    public record ProyectoGradoResumen(UUID id, String nombre) {
    }

    public record AsesorResumen(UUID id, String nombre, String email) {
    }

    public record EstadoEntregableResumen(String id, String nombre) {
    }
}
