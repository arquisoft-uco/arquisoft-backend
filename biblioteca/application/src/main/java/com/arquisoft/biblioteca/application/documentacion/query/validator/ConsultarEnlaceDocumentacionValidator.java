package com.arquisoft.biblioteca.application.documentacion.query.validator;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;

public interface ConsultarEnlaceDocumentacionValidator {

    void validar(EnlaceDocumentacionCriteria criteria, ArchivoDocumentacion archivo);
}
