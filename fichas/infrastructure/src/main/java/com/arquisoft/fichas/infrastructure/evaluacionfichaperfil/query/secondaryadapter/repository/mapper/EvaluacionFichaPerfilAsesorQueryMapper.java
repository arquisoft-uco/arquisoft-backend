package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository.EvaluacionFichaPerfilAsesorJpaQueryEntity;

public final class EvaluacionFichaPerfilAsesorQueryMapper {

    private EvaluacionFichaPerfilAsesorQueryMapper() {}

    public static EvaluacionFichaPerfilAsesorReadModel toReadModel(EvaluacionFichaPerfilAsesorJpaQueryEntity entity) {
        return new EvaluacionFichaPerfilAsesorReadModel(
                entity.getId(),
                entity.getFichaPerfilId(),
                entity.getTituloProyecto(),
                entity.getFechaCreacion(),
                entity.getEstadoEvaluacionId(),
                entity.getEstadoEvaluacionNombre(),
                new RepresentanteComiteReadModel(entity.getRepresentanteComiteId(), entity.getRepresentanteComiteNombre()));
    }
}
