package com.arquisoft.biblioteca.application.documentacion.query.validator;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;

public interface DescargarDocumentacionValidator {

    void validar(DescargaDocumentacionCriteria criteria, ArchivoDocumentacion archivo);
}
