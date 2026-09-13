package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;

import java.util.UUID;

public record EvaluacionCuantitativaJuradoReadModel(
        UUID id,
        Integer puntaje,
        ItemCuantitativoJuradoReadModel item
) {
}
