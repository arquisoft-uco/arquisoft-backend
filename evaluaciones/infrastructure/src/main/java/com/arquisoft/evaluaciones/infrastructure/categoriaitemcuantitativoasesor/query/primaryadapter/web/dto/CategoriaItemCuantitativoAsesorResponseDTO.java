package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativoasesor.query.primaryadapter.web.dto;

import java.util.UUID;

public record CategoriaItemCuantitativoAsesorResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
