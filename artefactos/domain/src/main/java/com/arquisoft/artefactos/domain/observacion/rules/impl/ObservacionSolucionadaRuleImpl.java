package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoSolucionadaException;
import com.arquisoft.artefactos.domain.observacion.model.EstadoActualObservacion;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionSolucionadaRule;

public class ObservacionSolucionadaRuleImpl implements ObservacionSolucionadaRule {

    @Override
    public void validar(EstadoActualObservacion entrada) {
        if (entrada.estado() != EstadoObservacion.SOLUCIONADA) {
            throw new ObservacionNoSolucionadaException(entrada.observacion(), entrada.estado());
        }
    }
}
