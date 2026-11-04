package com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel;

import java.util.UUID;

public record ItemCualitativoAsesorReadModel(
        UUID id,
        String nombre,
        String descripcion
) {
}
