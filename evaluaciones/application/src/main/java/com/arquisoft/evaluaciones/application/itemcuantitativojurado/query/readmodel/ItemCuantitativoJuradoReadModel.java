package com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel;

import java.util.UUID;

public record ItemCuantitativoJuradoReadModel(
        UUID id,
        String nombre,
        String descripcion,
        UUID categoriaId,
        Integer valor
) {
}
