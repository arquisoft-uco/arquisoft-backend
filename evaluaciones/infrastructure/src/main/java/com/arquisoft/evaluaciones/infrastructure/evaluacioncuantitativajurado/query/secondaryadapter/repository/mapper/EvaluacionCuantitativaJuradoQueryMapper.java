package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.readmodel.EvaluacionCuantitativaJuradoReadModel;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.query.readmodel.ItemCuantitativoJuradoReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.query.secondaryadapter.repository.EvaluacionCuantitativaJuradoJpaQueryEntity;

public final class EvaluacionCuantitativaJuradoQueryMapper {

    private EvaluacionCuantitativaJuradoQueryMapper() {}

    public static EvaluacionCuantitativaJuradoReadModel toReadModel(EvaluacionCuantitativaJuradoJpaQueryEntity entity) {
        return new EvaluacionCuantitativaJuradoReadModel(
                entity.getId(),
                entity.getPuntaje(),
                new ItemCuantitativoJuradoReadModel(
                        entity.getItemId(),
                        entity.getItemNombre(),
                        entity.getItemDescripcion(),
                        entity.getItemCategoriaId(),
                        entity.getItemValor()));
    }
}
