package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.EnlaceDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.validator.ConsultarEnlaceDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.model.EstadoPublicacionDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionCompartibleRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.DocumentacionCompartibleRuleImpl;
import com.arquisoft.biblioteca.domain.estadodocumentacion.EstadoDocumentacion;
import org.springframework.stereotype.Component;

@Component
public class ConsultarEnlaceDocumentacionValidatorImpl implements ConsultarEnlaceDocumentacionValidator {

    private final DocumentacionCompartibleRule documentacionCompartibleRule;

    public ConsultarEnlaceDocumentacionValidatorImpl() {
        this.documentacionCompartibleRule = new DocumentacionCompartibleRuleImpl();
    }

    @Override
    public void validar(EnlaceDocumentacionCriteria criteria, EstadoDocumentacion estado) {
        documentacionCompartibleRule.validar(
                new EstadoPublicacionDocumentacion(criteria.documentacion(), estado));
    }
}
