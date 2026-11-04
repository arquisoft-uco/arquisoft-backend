package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record ItemCualitativoAsesorResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
