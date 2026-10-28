package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity;

import java.util.UUID;

public record EntregableProyectoGradoEntity(
        UUID id,
        UUID proyectoGrado,
        int version,
        UUID asesor,
        String estadoEntregable
) {
}
