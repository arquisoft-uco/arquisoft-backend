package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity.ArchivoDocumentacionAccesoEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.ArchivoDocumentacionJpaQueryEntity;

public final class ArchivoDocumentacionAccesoQueryMapper {

    private ArchivoDocumentacionAccesoQueryMapper() {}

    public static ArchivoDocumentacionAccesoEntity toEntity(ArchivoDocumentacionJpaQueryEntity jpaEntity) {
        return new ArchivoDocumentacionAccesoEntity(
                jpaEntity.getId(),
                jpaEntity.getEstadoId(),
                jpaEntity.getBucket(),
                jpaEntity.getClaveObjeto(),
                jpaEntity.getNombreArchivo());
    }
}
