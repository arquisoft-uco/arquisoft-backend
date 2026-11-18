package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.EstadoDestinoDocumentacionNoPermitidoException;
import com.arquisoft.biblioteca.domain.documentacion.model.TransicionEstadoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.EstadoDestinoDocumentacionPermitidoRule;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public class EstadoDestinoDocumentacionPermitidoRuleImpl implements EstadoDestinoDocumentacionPermitidoRule {

    @Override
    public void validar(TransicionEstadoDocumentacion transicion) {
        var nuevo = transicion.nuevo();
        var permitido = nuevo == EstadoDocumentacion.PUBLICADO || nuevo == EstadoDocumentacion.NO_PUBLICADO;
        if (!permitido) {
            throw new EstadoDestinoDocumentacionNoPermitidoException(transicion.documentacion(), nuevo);
        }
    }
}
