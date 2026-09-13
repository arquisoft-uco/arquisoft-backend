package com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel;

import java.util.UUID;

public record CriterioItemCualitativoJuradoReadModel(
        UUID id,
        String nombre,
        String descripcion
) {
}
