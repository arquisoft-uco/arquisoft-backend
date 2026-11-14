package com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.infrastructure.artefacto.query.secondaryadapter.repository.ArtefactoJpaQueryEntity;

public final class ArtefactoQueryMapper {

    private ArtefactoQueryMapper() {}

    public static ArtefactoVistaPreviaReadModel toReadModel(ArtefactoJpaQueryEntity entity) {
        return new ArtefactoVistaPreviaReadModel(entity.getId(), entity.getNombreDocumento(), entity.getNumeroVersion());
    }
}
