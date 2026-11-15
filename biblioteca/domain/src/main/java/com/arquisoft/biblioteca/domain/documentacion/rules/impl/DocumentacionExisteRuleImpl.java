package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoEncontradaException;
import com.arquisoft.biblioteca.domain.documentacion.model.ExistenciaDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionExisteRule;

public class DocumentacionExisteRuleImpl implements DocumentacionExisteRule {

    @Override
    public void validar(ExistenciaDocumentacion existencia) {
        if (!existencia.existe()) {
            throw new DocumentacionNoEncontradaException(existencia.documentacion());
        }
    }
}
