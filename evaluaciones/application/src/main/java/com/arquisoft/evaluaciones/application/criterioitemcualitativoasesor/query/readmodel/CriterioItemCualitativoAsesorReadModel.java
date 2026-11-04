package com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel;

import java.util.UUID;

public record CriterioItemCualitativoAsesorReadModel(
        UUID id,
        String nombre,
        String descripcion
) {
}
