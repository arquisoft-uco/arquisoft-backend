package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.readmodel;

import java.time.Instant;
import java.util.UUID;

public record EstadoEntregableProyectoGradoReadModel(
        UUID id,
        String estadoEntregable,
        String estadoEntregableNombre,
        Instant fechaActualizacion
) {
}
