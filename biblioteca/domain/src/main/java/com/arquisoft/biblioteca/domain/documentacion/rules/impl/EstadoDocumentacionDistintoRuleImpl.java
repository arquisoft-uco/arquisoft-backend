package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDocumentacionSinCambioException;
import com.arquisoft.biblioteca.domain.documentacion.model.TransicionEstadoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.EstadoDocumentacionDistintoRule;

public class EstadoDocumentacionDistintoRuleImpl implements EstadoDocumentacionDistintoRule {

    @Override
    public void validar(TransicionEstadoDocumentacion transicion) {
        if (transicion.nuevo() == transicion.actual()) {
            throw new EstadoDocumentacionSinCambioException(transicion.documentacion(), transicion.nuevo());
        }
    }
}
