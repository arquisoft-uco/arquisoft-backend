package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.EvaluacionFichaPerfilCoordinadorJpaQueryEntity;

public final class EvaluacionFichaPerfilCoordinadorQueryMapper {

    private EvaluacionFichaPerfilCoordinadorQueryMapper() {}

    public static EvaluacionFichaPerfilCoordinadorReadModel toReadModel(EvaluacionFichaPerfilCoordinadorJpaQueryEntity entity) {
        return new EvaluacionFichaPerfilCoordinadorReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion(),
                entity.getEstadoEvaluacionId(),
                entity.getEstadoEvaluacionNombre(),
                new RepresentanteComiteReadModel(entity.getRepresentanteComiteId(), entity.getRepresentanteComiteNombre()));
    }
}
