package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity.DocumentacionJpaEntity;

public final class DocumentacionJpaMapper {

    private DocumentacionJpaMapper() {}

    public static DocumentacionEntity toEntity(DocumentacionJpaEntity jpaEntity) {
        return new DocumentacionEntity(
                jpaEntity.getId(),
                jpaEntity.getUrl(),
                jpaEntity.getFechaPublicacion(),
                jpaEntity.getEntregableProyectoGradoId(),
                jpaEntity.getBibliotecarioId(),
                jpaEntity.getEstadoDocumentacion());
    }

    public static DocumentacionJpaEntity toJpaEntity(DocumentacionEntity entity) {
        return DocumentacionJpaEntity.builder()
                .id(entity.id())
                .url(entity.url())
                .fechaPublicacion(entity.fechaPublicacion())
                .entregableProyectoGradoId(entity.entregableProyectoGrado())
                .bibliotecarioId(entity.bibliotecario())
                .estadoDocumentacion(entity.estadoDocumentacion())
                .build();
    }
}
