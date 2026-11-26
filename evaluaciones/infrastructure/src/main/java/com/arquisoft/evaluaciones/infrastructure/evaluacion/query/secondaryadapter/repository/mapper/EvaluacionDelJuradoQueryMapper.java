package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository.EvaluacionDelJuradoJpaQueryEntity;

import com.arquisoft.shared.util.UtilObjeto;

public final class EvaluacionDelJuradoQueryMapper {

    private EvaluacionDelJuradoQueryMapper() {}

    public static EvaluacionReadModel toReadModel(EvaluacionDelJuradoJpaQueryEntity entity) {
        var resultado = UtilObjeto.noEsNulo(entity.getResultadoId())
                ? new EvaluacionReadModel.Resultado(entity.getResultadoId(), entity.getResultadoNombre())
                : null;
        return new EvaluacionReadModel(
                entity.getId(),
                new EvaluacionReadModel.Entregable(
                        entity.getEntregableId(), entity.getEntregableProyecto(), entity.getEntregableVersion()),
                new EvaluacionReadModel.Estado(entity.getEstadoId(), entity.getEstadoNombre()),
                new EvaluacionReadModel.Notas(entity.getNotaAsesor(), entity.getNotaJurados(), entity.getNotaFinal()),
                resultado);
    }
}
