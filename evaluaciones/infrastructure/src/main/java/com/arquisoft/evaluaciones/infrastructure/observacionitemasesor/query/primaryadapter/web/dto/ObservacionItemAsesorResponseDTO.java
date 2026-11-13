package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record ObservacionItemAsesorResponseDTO(
        UUID id,
        String descripcion
) {
}
