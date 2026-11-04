package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record ItemCuantitativoAsesorResponseDTO(
        UUID id,
        String nombre,
        String descripcion,
        UUID categoriaId,
        Integer valor
) {
}
