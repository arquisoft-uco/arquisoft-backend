package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoCompartibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.EstadoPublicacionDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionCompartibleRule;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public class DocumentacionCompartibleRuleImpl implements DocumentacionCompartibleRule {

    @Override
    public void validar(EstadoPublicacionDocumentacion entrada) {
        if (entrada.estado() != EstadoDocumentacion.PUBLICADO) {
            throw new DocumentacionNoCompartibleException(entrada.documentacion());
        }
    }
}
