package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record CriterioItemCualitativoAsesorResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
