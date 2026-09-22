package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;

import java.util.UUID;

public record EvaluacionCualitativaJuradoReadModel(
        UUID id,
        ItemCualitativoJuradoReadModel item,
        CriterioItemCualitativoJuradoReadModel criterio
) {
}
