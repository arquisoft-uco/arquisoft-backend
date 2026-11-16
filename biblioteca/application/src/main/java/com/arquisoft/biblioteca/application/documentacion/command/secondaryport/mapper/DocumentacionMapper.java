package com.arquisoft.biblioteca.application.documentacion.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public final class DocumentacionMapper {

    private DocumentacionMapper() {}

    public static DocumentacionEntity toEntity(DocumentacionDomain documentacion) {
        return new DocumentacionEntity(
                documentacion.getId(),
                documentacion.getUrl(),
                documentacion.getFechaPublicacion(),
                documentacion.getEntregableProyectoGrado(),
                documentacion.getBibliotecario(),
                documentacion.getEstadoDocumentacion().getId());
    }

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
