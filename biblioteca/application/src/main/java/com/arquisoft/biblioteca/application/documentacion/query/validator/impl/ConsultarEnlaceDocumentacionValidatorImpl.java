package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.model.EstadoPublicacionDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.ArchivoDocumentacionDisponibleRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionCompartibleRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.ArchivoDocumentacionDisponibleRuleImpl;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.DocumentacionCompartibleRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class ConsultarEnlaceDocumentacionValidatorImpl implements ConsultarEnlaceDocumentacionValidator {

    private final DocumentacionCompartibleRule documentacionCompartibleRule;
    private final ArchivoDocumentacionDisponibleRule archivoDocumentacionDisponibleRule;

    public ConsultarEnlaceDocumentacionValidatorImpl() {
        this.documentacionCompartibleRule = new DocumentacionCompartibleRuleImpl();
        this.archivoDocumentacionDisponibleRule = new ArchivoDocumentacionDisponibleRuleImpl();
    }

    @Override
    public void validar(EnlaceDocumentacionCriteria criteria, ArchivoDocumentacion archivo) {
        documentacionCompartibleRule.validar(
                new EstadoPublicacionDocumentacion(criteria.documentacion(), archivo.estado()));
        archivoDocumentacionDisponibleRule.validar(archivo);
    }
}
