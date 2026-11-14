package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.query.readmodel.CriterioItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.query.readmodel.EvaluacionCualitativaAsesorEstudianteReadModel;
import com.arquisoft.evaluaciones.application.itemcualitativoasesor.query.readmodel.ItemCualitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.query.secondaryadapter.repository.EvaluacionCualitativaAsesorEstudianteJpaQueryEntity;

public final class EvaluacionCualitativaAsesorEstudianteQueryMapper {

    private EvaluacionCualitativaAsesorEstudianteQueryMapper() {}

    public static EvaluacionCualitativaAsesorEstudianteReadModel toReadModel(
            EvaluacionCualitativaAsesorEstudianteJpaQueryEntity entity) {
        return new EvaluacionCualitativaAsesorEstudianteReadModel(
                entity.getId(),
                new ItemCualitativoAsesorReadModel(
                        entity.getItemId(), entity.getItemNombre(), entity.getItemDescripcion()),
                new CriterioItemCualitativoAsesorReadModel(
                        entity.getCriterioId(), entity.getCriterioNombre(), entity.getCriterioDescripcion()));
    }
}
