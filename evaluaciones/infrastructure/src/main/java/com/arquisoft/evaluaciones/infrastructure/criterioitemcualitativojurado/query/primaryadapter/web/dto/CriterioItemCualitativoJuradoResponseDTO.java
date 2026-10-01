package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record CriterioItemCualitativoJuradoResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
