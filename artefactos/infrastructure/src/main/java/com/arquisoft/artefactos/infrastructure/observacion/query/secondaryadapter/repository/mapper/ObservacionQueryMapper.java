package com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.ObservacionEstudianteJpaQueryEntity;
import com.arquisoft.artefactos.infrastructure.observacion.query.secondaryadapter.repository.ObservacionJpaQueryEntity;

public final class ObservacionQueryMapper {

    private ObservacionQueryMapper() {}

    public static ObservacionReadModel toReadModel(ObservacionJpaQueryEntity entity) {
        return new ObservacionReadModel(
                entity.getId(),
                entity.getRevisionAsesor(),
                entity.getDescripcion(),
                entity.getEstado(),
                entity.getEstadoNombre());
    }

    public static ObservacionReadModel toReadModel(ObservacionEstudianteJpaQueryEntity entity) {
        return new ObservacionReadModel(
                entity.getId(),
                entity.getRevisionAsesor(),
                entity.getDescripcion(),
                entity.getEstado(),
                entity.getEstadoNombre());
    }
}
