package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;

import java.util.UUID;

public final class ConsultarAutoresDocumentacionRequestMapper {

    private ConsultarAutoresDocumentacionRequestMapper() {}

    public static ConsultarAutoresDocumentacionQuery toQuery(UUID documentacionId) {
        return ConsultarAutoresDocumentacionQuery.crear(documentacionId);
    }
}
