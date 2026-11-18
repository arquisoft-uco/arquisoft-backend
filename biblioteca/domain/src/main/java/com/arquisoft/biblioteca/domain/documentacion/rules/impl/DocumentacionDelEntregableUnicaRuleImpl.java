package com.arquisoft.biblioteca.domain.documentacion.rules.impl;

import com.arquisoft.biblioteca.domain.documentacion.exception.DocumentacionYaRegistradaException;
import com.arquisoft.biblioteca.domain.documentacion.model.UnicidadDocumentacionEntregable;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionDelEntregableUnicaRule;

public class DocumentacionDelEntregableUnicaRuleImpl implements DocumentacionDelEntregableUnicaRule {

    @Override
    public void validar(UnicidadDocumentacionEntregable unicidad) {
        if (unicidad.existe()) {
            throw new DocumentacionYaRegistradaException(unicidad.entregableProyectoGrado());
        }
    }
}
