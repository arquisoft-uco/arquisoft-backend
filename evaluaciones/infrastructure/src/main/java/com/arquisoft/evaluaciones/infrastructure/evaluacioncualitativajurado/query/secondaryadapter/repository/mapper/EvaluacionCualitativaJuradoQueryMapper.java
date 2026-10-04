package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.query.readmodel.CriterioItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.readmodel.EvaluacionCualitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativojurado.query.readmodel.ItemCualitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.query.secondaryadapter.repository.EvaluacionCualitativaJuradoJpaQueryEntity;

public final class EvaluacionCualitativaJuradoQueryMapper {

    private EvaluacionCualitativaJuradoQueryMapper() {}

    public static EvaluacionCualitativaJuradoReadModel toReadModel(EvaluacionCualitativaJuradoJpaQueryEntity entity) {
        return new EvaluacionCualitativaJuradoReadModel(
                entity.getId(),
                new ItemCualitativoJuradoReadModel(
                        entity.getItemId(), entity.getItemNombre(), entity.getItemDescripcion()),
                new CriterioItemCualitativoJuradoReadModel(
                        entity.getCriterioId(), entity.getCriterioNombre(), entity.getCriterioDescripcion()));
    }
}
