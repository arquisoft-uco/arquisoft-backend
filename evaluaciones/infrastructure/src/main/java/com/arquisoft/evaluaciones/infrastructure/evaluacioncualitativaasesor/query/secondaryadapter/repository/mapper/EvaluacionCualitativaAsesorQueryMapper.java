package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.EvaluacionCualitativaAsesorJpaQueryEntity;

public final class EvaluacionCualitativaAsesorQueryMapper {

    private EvaluacionCualitativaAsesorQueryMapper() {}

    public static EvaluacionCualitativaAsesorReadModel toReadModel(EvaluacionCualitativaAsesorJpaQueryEntity entity) {
        return new EvaluacionCualitativaAsesorReadModel(
                entity.getId(),
                new ItemCualitativoAsesorReadModel(
                        entity.getItemId(), entity.getItemNombre(), entity.getItemDescripcion()),
                new CriterioItemCualitativoAsesorReadModel(
                        entity.getCriterioId(), entity.getCriterioNombre(), entity.getCriterioDescripcion()));
    }
}
