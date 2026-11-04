package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto;

import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

public record ConsultarArtefactosSinAvanceRequestDTO(
        Integer diasSinAvance,
        QueryCriteriaRequestDTO criterio
) {
}
