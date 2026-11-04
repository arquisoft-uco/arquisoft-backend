package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;

import java.util.UUID;

public record EvaluacionCualitativaAsesorReadModel(
        UUID id,
        ItemCualitativoAsesorReadModel item,
        CriterioItemCualitativoAsesorReadModel criterio
) {
}
