package com.arquisoft.biblioteca.application.documentacion.command.validator;

import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;

public interface CambiarEstadoDocumentacionValidator {

    void validar(CambioEstadoDocumentacionDomain cambio, DocumentacionDomain documentacion);
}
