package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.query.readmodel.EvaluacionCuantitativaAsesorReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.query.readmodel.ItemCuantitativoAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.query.secondaryadapter.repository.EvaluacionCuantitativaAsesorJpaQueryEntity;

public final class EvaluacionCuantitativaAsesorQueryMapper {

    private EvaluacionCuantitativaAsesorQueryMapper() {}

    public static EvaluacionCuantitativaAsesorReadModel toReadModel(EvaluacionCuantitativaAsesorJpaQueryEntity entity) {
        return new EvaluacionCuantitativaAsesorReadModel(
                entity.getId(),
                entity.getPuntaje(),
                new ItemCuantitativoAsesorReadModel(
                        entity.getItemId(),
                        entity.getItemNombre(),
                        entity.getItemDescripcion(),
                        entity.getItemCategoriaId(),
                        entity.getItemValor()));
    }
}
