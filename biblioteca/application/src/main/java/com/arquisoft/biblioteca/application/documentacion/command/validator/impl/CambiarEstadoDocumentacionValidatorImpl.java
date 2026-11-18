package com.arquisoft.biblioteca.application.documentacion.command.validator.impl;

import com.arquisoft.biblioteca.application.documentacion.command.validator.CambiarEstadoDocumentacionValidator;
import com.arquisoft.biblioteca.domain.documentacion.CambioEstadoDocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.DocumentacionDomain;
import com.arquisoft.biblioteca.domain.documentacion.model.ExistenciaDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.model.TransicionEstadoDocumentacion;
import com.arquisoft.biblioteca.domain.documentacion.rules.DocumentacionExisteRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.EstadoDestinoDocumentacionPermitidoRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.EstadoDocumentacionDistintoRule;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.DocumentacionExisteRuleImpl;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.EstadoDestinoDocumentacionPermitidoRuleImpl;
import com.arquisoft.biblioteca.domain.documentacion.rules.impl.EstadoDocumentacionDistintoRuleImpl;
import org.springframework.stereotype.Component;

@Component
public class CambiarEstadoDocumentacionValidatorImpl implements CambiarEstadoDocumentacionValidator {

    private final DocumentacionExisteRule documentacionExisteRule;
    private final EstadoDestinoDocumentacionPermitidoRule estadoDestinoDocumentacionPermitidoRule;
    private final EstadoDocumentacionDistintoRule estadoDocumentacionDistintoRule;

    public CambiarEstadoDocumentacionValidatorImpl() {
        this.documentacionExisteRule = new DocumentacionExisteRuleImpl();
        this.estadoDestinoDocumentacionPermitidoRule = new EstadoDestinoDocumentacionPermitidoRuleImpl();
        this.estadoDocumentacionDistintoRule = new EstadoDocumentacionDistintoRuleImpl();
    }

    @Override
    public void validar(CambioEstadoDocumentacionDomain cambio, DocumentacionDomain documentacion) {
        documentacionExisteRule.validar(
                new ExistenciaDocumentacion(cambio.getDocumentacion(), !documentacion.esVacio()));
        var transicion = new TransicionEstadoDocumentacion(
                cambio.getDocumentacion(), documentacion.getEstadoDocumentacion(), cambio.getEstado());
        estadoDestinoDocumentacionPermitidoRule.validar(transicion);
        estadoDocumentacionDistintoRule.validar(transicion);
    }
}
