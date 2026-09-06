package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record ItemCualitativoJuradoResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
