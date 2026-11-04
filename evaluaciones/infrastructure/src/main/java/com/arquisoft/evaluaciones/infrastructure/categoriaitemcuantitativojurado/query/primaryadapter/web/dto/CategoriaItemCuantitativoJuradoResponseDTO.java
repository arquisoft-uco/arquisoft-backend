package com.arquisoft.evaluaciones.infrastructure.categoriaitemcuantitativojurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record CategoriaItemCuantitativoJuradoResponseDTO(
        UUID id,
        String nombre,
        String descripcion
) {
}
