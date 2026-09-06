package com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel;

import java.util.UUID;

public record ItemCualitativoJuradoReadModel(
        UUID id,
        String nombre,
        String descripcion
) {
}
