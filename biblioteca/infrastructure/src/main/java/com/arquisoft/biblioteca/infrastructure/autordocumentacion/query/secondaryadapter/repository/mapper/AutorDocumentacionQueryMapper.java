package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository.AutorDocumentacionJpaQueryEntity;

public final class AutorDocumentacionQueryMapper {

    private AutorDocumentacionQueryMapper() {}

    public static AutorDocumentacionReadModel toReadModel(AutorDocumentacionJpaQueryEntity entity) {
        return new AutorDocumentacionReadModel(
                entity.getAutorId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail());
    }
}
