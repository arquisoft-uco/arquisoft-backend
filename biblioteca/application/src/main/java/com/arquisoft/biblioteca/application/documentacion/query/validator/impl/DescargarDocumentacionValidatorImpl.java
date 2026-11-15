package com.arquisoft.biblioteca.application.documentacion.query.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DescargaDocumentacionCriteria;
import com.arquisoft.biblioteca.application.documentacion.query.validator.DescargarDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.model.AccesoDescargaDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.model.ArchivoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.ArchivoDocumentacionDisponibleRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionDescargableRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.ArchivoDocumentacionDisponibleRuleImpl;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.DocumentacionDescargableRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class DescargarDocumentacionValidatorImpl implements DescargarDocumentacionValidator {

    private final DocumentacionDescargableRule documentacionDescargableRule;
    private final ArchivoDocumentacionDisponibleRule archivoDocumentacionDisponibleRule;

    public DescargarDocumentacionValidatorImpl() {
        this.documentacionDescargableRule = new DocumentacionDescargableRuleImpl();
        this.archivoDocumentacionDisponibleRule = new ArchivoDocumentacionDisponibleRuleImpl();
    }

    @Override
    public void validar(DescargaDocumentacionCriteria criteria, ArchivoDocumentacion archivo) {
        documentacionDescargableRule.validar(new AccesoDescargaDocumentacion(
                criteria.documentacion(), archivo.estado(), criteria.comoBibliotecario()));
        archivoDocumentacionDisponibleRule.validar(archivo);
    }
}
