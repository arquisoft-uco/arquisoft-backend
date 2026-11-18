package com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.entity.AutorDocumentacionJpaEntity;

public final class AutorDocumentacionJpaMapper {

    private AutorDocumentacionJpaMapper() {}

    public static AutorDocumentacionJpaEntity toJpaEntity(AutorDocumentacionEntity entity) {
        return AutorDocumentacionJpaEntity.builder()
                .id(entity.id())
                .autorId(entity.autor())
                .documentacionId(entity.documentacion())
                .build();
    }
}
