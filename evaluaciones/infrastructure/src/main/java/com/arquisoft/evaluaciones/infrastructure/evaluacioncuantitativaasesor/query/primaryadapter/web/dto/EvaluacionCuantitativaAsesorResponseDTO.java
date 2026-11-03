package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.primaryadapter.web.dto;

import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.dto.ItemCuantitativoAsesorResponseDTO;
import java.util.UUID;

public record EvaluacionCuantitativaAsesorResponseDTO(
        UUID id,
        Integer puntaje,
        ItemCuantitativoAsesorResponseDTO item
) {
}
