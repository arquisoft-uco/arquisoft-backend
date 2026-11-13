package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.ConsultarEnlaceDocumentacionQuery;

public final class ConsultarEnlaceDocumentacionMapper {

    private ConsultarEnlaceDocumentacionMapper() {}

    public static EnlaceDocumentacionCriteria toCriteria(ConsultarEnlaceDocumentacionQuery query) {
        return new EnlaceDocumentacionCriteria(query.documentacion());
    }
}
