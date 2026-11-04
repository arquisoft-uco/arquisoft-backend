package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record ObservacionItemJuradoResponseDTO(
        UUID id,
        String descripcion
) {
}
