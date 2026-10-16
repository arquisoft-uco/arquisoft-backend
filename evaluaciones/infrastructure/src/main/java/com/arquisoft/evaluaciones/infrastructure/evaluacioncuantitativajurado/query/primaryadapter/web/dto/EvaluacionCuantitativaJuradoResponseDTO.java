package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.primaryadapter.web.dto;

import java.util.UUID;

public record EvaluacionCuantitativaJuradoResponseDTO(
        UUID id,
        Integer puntaje,
        ItemDTO item
) {

    public record ItemDTO(UUID id, String nombre, String descripcion, UUID categoriaId, Integer valor) {
    }
}
