package com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.autordocumentacion.query.criteria.AutorDocumentacionCriteria;
import com.arquisoft.biblioteca.application.autordocumentacion.query.primaryport.model.ConsultarAutoresDocumentacionQuery;

public final class ConsultarAutoresDocumentacionMapper {

    private ConsultarAutoresDocumentacionMapper() {}

    public static AutorDocumentacionCriteria toCriteria(ConsultarAutoresDocumentacionQuery query) {
        return new AutorDocumentacionCriteria(query.documentacion());
    }
}
