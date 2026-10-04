package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.EvaluacionFichaPerfilEstudianteJpaQueryEntity;

public final class EvaluacionFichaPerfilEstudianteQueryMapper {

    private EvaluacionFichaPerfilEstudianteQueryMapper() {}

    public static EvaluacionFichaPerfilEstudianteReadModel toReadModel(EvaluacionFichaPerfilEstudianteJpaQueryEntity entity) {
        return new EvaluacionFichaPerfilEstudianteReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion(),
                entity.getEstadoEvaluacionId(),
                entity.getEstadoEvaluacionNombre(),
                new RepresentanteComiteReadModel(entity.getRepresentanteComiteId(), entity.getRepresentanteComiteNombre()));
    }
}
