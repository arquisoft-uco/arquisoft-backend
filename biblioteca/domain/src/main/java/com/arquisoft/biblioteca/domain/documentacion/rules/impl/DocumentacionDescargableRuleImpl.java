package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionNoDescargableException;
import com.arquisoft.biblioteca.domain.documentacion.model.AccesoDescargaDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionDescargableRule;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;

public class DocumentacionDescargableRuleImpl implements DocumentacionDescargableRule {

    @Override
    public void validar(AccesoDescargaDocumentacion entrada) {
        var inexistente = entrada.estado() == EstadoDocumentacion.VACIO;
        var noPermitida = entrada.estado() != EstadoDocumentacion.PUBLICADO && !entrada.comoBibliotecario();

        if (inexistente || noPermitida) {
            throw new DocumentacionNoDescargableException(entrada.documentacion());
        }
    }
}
