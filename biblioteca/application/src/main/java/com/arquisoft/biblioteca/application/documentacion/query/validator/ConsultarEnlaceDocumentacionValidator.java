package com.arquisoft.biblioteca.application.documentacion.query.validator;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public interface ConsultarEnlaceDocumentacionValidator {

    void validar(EnlaceDocumentacionCriteria criteria, EstadoDocumentacion estado);
}
