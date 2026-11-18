package com.arquisoft.biblioteca.infrastructure.documentacion.query.primaryadapter.web.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;

import java.util.UUID;

public final class ConsultarEnlaceDocumentacionRequestMapper {

    private ConsultarEnlaceDocumentacionRequestMapper() {}

    public static ConsultarEnlaceDocumentacionQuery toQuery(UUID documentacionId) {
        return ConsultarEnlaceDocumentacionQuery.crear(documentacionId);
    }
}
