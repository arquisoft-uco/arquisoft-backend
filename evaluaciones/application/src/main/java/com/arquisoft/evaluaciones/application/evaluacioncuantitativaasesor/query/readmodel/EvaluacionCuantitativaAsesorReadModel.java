package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;

import java.util.UUID;

public record EvaluacionCuantitativaAsesorReadModel(
        UUID id,
        Integer puntaje,
        ItemCuantitativoAsesorReadModel item
) {
}
