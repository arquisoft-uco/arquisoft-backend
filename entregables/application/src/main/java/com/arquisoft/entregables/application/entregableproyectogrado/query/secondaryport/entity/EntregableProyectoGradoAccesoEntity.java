package com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.entity;

import java.util.UUID;

public record EntregableProyectoGradoAccesoEntity(
        UUID id,
        UUID proyectoGrado,
        int version,
        UUID asesor,
        String estadoEntregable
) {
}
