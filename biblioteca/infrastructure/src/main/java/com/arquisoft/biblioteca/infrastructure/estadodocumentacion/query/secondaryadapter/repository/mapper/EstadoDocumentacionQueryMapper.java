package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository.EstadoDocumentacionJpaQueryEntity;

public final class EstadoDocumentacionQueryMapper {

    private EstadoDocumentacionQueryMapper() {}

    public static EstadoDocumentacionReadModel toReadModel(EstadoDocumentacionJpaQueryEntity entity) {
        return new EstadoDocumentacionReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
