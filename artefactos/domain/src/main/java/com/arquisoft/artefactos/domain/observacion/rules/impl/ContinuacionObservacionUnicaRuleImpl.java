package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionDuplicadaException;
import com.arquisoft.artefactos.domain.observacion.model.UnicidadContinuacionObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ContinuacionObservacionUnicaRule;

public class ContinuacionObservacionUnicaRuleImpl implements ContinuacionObservacionUnicaRule {

    @Override
    public void validar(UnicidadContinuacionObservacion entrada) {
        if (entrada.destino().esReabierta() && entrada.existe()) {
            throw new ObservacionDuplicadaException(entrada.revisionAsesor());
        }
    }
}
