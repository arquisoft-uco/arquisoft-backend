package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.EstadoDestinoObservacionEstudianteNoPermitidoException;
import com.arquisoft.artefactos.domain.observacion.model.DestinoCambioEstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.EstadoDestinoObservacionEstudiantePermitidoRule;

public class EstadoDestinoObservacionEstudiantePermitidoRuleImpl
        implements EstadoDestinoObservacionEstudiantePermitidoRule {

    @Override
    public void validar(DestinoCambioEstadoObservacion entrada) {
        if (entrada.estado() != EstadoObservacion.EN_PROGRESO && entrada.estado() != EstadoObservacion.SOLUCIONADA) {
            throw new EstadoDestinoObservacionEstudianteNoPermitidoException(
                    entrada.observacion(), entrada.estado());
        }
    }
}
