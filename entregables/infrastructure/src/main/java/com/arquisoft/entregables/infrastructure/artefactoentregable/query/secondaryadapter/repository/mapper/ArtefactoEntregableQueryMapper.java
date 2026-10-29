package com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.secondaryadapter.repository.ArtefactoEntregableJpaQueryEntity;

public final class ArtefactoEntregableQueryMapper {

    private ArtefactoEntregableQueryMapper() {}

    public static ArtefactoEntregableReadModel toReadModel(ArtefactoEntregableJpaQueryEntity entity) {
        return new ArtefactoEntregableReadModel(
                entity.getId(),
                entity.getEntregableId(),
                entity.getArtefactoId(),
                entity.getUriArchivo(),
                entity.getIdArtefacto(),
                entity.getIdRepositorioArtefacto(),
                entity.getNombreDocumento(),
                entity.getIdRepositorio(),
                entity.getNombreRepositorio());
    }
}
