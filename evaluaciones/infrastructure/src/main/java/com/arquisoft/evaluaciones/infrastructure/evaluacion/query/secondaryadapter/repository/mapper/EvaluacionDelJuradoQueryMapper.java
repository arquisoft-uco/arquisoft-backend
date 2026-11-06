package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionDelJuradoJpaQueryEntity;

public final class EvaluacionDelJuradoQueryMapper {

    private EvaluacionDelJuradoQueryMapper() {}

    public static EvaluacionReadModel toReadModel(EvaluacionDelJuradoJpaQueryEntity entity) {
        return new EvaluacionReadModel(
                entity.getId(),
                new EvaluacionReadModel.Entregable(
                        entity.getEntregableId(), entity.getEntregableProyecto(), entity.getEntregableVersion()),
                new EvaluacionReadModel.Estado(entity.getEstadoId(), entity.getEstadoNombre()));
    }
}
