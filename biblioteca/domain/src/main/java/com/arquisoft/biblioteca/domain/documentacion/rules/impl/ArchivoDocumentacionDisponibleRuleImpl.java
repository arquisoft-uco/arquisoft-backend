package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.ArchivoDocumentacionNoDisponibleException;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.ArchivoDocumentacionDisponibleRule;
import com.arquisoft.shared.util.UtilTexto;

public class ArchivoDocumentacionDisponibleRuleImpl implements ArchivoDocumentacionDisponibleRule {

    @Override
    public void validar(ArchivoDocumentacion entrada) {
        if (UtilTexto.esVacioONulo(entrada.claveObjeto())) {
            throw new ArchivoDocumentacionNoDisponibleException(entrada.documentacion());
        }
    }
}
