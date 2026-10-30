package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel;

import java.util.UUID;

public record ItemCuantitativoAsesorReadModel(
        UUID id,
        String nombre,
        String descripcion,
        UUID categoriaId,
        Integer valor
) {
}
