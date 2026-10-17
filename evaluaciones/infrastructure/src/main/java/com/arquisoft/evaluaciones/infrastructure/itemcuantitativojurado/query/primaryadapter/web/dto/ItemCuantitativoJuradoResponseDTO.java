package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record ItemCuantitativoJuradoResponseDTO(
        UUID id,
        String nombre,
        String descripcion,
        UUID categoriaId,
        Integer valor
) {
}
