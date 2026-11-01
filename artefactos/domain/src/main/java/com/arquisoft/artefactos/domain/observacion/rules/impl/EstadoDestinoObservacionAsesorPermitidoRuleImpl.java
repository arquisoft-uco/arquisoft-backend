package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.EstadoDestinoObservacionAsesorPermitidoRule;

public class EstadoDestinoObservacionAsesorPermitidoRuleImpl
        implements EstadoDestinoObservacionAsesorPermitidoRule {

    @Override
    public void validar(DestinoCambioEstadoObservacion entrada) {
        if (entrada.estado() != EstadoObservacion.CERRADA && !entrada.estado().esReabierta()) {
            throw new EstadoDestinoObservacionNoPermitidoException(entrada.observacion(), entrada.estado());
        }
    }
}
