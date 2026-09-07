package com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EntregableProyectoAccesoEntity(
        UUID entregable,
        UUID proyecto,
        int versionEntregable,
        boolean activo,
        Instant ocurridoEn
) {
}
