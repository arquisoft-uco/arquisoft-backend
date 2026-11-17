package com.arquisoft.biblioteca.application.documentacion.query.primaryport.mapper;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.DescargarDocumentacionQuery;
import com.arquisoft.biblioteca.application.documentacion.query.primaryport.model.RolConsultaDocumentacion;

public final class DescargarDocumentacionMapper {

    private DescargarDocumentacionMapper() {}

    public static DescargaDocumentacionCriteria toCriteria(DescargarDocumentacionQuery query) {
        return new DescargaDocumentacionCriteria(
                query.documentacion(),
                query.roles().contains(RolConsultaDocumentacion.BIBLIOTECARIO));
    }
}
