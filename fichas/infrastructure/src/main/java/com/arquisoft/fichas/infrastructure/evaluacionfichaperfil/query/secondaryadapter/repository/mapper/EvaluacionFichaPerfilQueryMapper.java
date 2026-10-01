package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.EvaluacionFichaPerfilJpaQueryEntity;

public final class EvaluacionFichaPerfilQueryMapper {

    private EvaluacionFichaPerfilQueryMapper() {}

    public static EvaluacionFichaPerfilReadModel toReadModel(EvaluacionFichaPerfilJpaQueryEntity entity) {
        return new EvaluacionFichaPerfilReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion(),
                entity.getEstadoEvaluacionId(),
                entity.getEstadoEvaluacionNombre());
    }
}
