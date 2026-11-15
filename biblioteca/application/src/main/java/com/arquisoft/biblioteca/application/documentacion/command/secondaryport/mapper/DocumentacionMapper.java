package com.arquisoft.biblioteca.application.documentacion.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public final class DocumentacionMapper {

    private DocumentacionMapper() {}

    public static DocumentacionDomain toDomain(DocumentacionEntity entity) {
        return DocumentacionDomain.reconstruir(
                entity.id(),
                entity.url(),
                entity.fechaPublicacion(),
                entity.entregableProyectoGrado(),
                entity.bibliotecario(),
                EstadoDocumentacion.desde(entity.estadoDocumentacion()));
    }
}
