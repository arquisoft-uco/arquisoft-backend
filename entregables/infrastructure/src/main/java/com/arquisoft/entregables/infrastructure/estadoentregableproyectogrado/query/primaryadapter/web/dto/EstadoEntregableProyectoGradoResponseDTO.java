package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.dto;

import java.time.Instant;
import java.util.UUID;

public record EstadoEntregableProyectoGradoResponseDTO(
        UUID id,
        String estadoEntregable,
        String estadoEntregableNombre,
        Instant fechaActualizacion
) {
}
