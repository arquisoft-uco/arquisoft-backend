package com.arquisoft.fichas.infrastructure.representantecomite.query.primaryadapter.web.dto;

import java.util.UUID;

public record RepresentanteComiteResponseDTO(
        UUID id,
        String nombre
) {
}
