package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEditablePorEstudianteException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionEditablePorEstudianteRule;

public class ObservacionEditablePorEstudianteRuleImpl implements ObservacionEditablePorEstudianteRule {

    @Override
    public void validar(EstadoActualObservacion entrada) {
        if (entrada.estado() == EstadoObservacion.CERRADA || entrada.estado().esReabierta()) {
            throw new ObservacionNoEditablePorEstudianteException(entrada.observacion(), entrada.estado());
        }
    }
}
