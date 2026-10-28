package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.EstadoDestinoRevisionAsesorNoPermitidoException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.DestinoCambioEstado;
import com.arquisoft.artefactos.domain.revisionasesor.rules.EstadoDestinoRevisionAsesorPermitidoRule;

public class EstadoDestinoRevisionAsesorPermitidoRuleImpl implements EstadoDestinoRevisionAsesorPermitidoRule {

    @Override
    public void validar(DestinoCambioEstado entrada) {
        if (entrada.estado() == EstadoRevisionAsesor.CERRADA) {
            throw new EstadoDestinoRevisionAsesorNoPermitidoException(entrada.revisionAsesor(), entrada.estado());
        }
    }
}
