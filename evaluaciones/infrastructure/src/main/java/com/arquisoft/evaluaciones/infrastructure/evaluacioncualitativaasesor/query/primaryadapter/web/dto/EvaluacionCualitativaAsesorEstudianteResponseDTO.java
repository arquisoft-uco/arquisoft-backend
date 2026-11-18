package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.primaryadapter.web.dto;

import com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.primaryadapter.web.dto.CriterioItemCualitativoAsesorResponseDTO;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.primaryadapter.web.dto.ItemCualitativoAsesorResponseDTO;
import java.util.UUID;

public record EvaluacionCualitativaAsesorEstudianteResponseDTO(
        UUID id,
        ItemCualitativoAsesorResponseDTO item,
        CriterioItemCualitativoAsesorResponseDTO criterio
) {
}
