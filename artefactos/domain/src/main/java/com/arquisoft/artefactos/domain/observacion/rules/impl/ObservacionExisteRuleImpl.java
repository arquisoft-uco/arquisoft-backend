package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoEncontradaException;
import com.arquisoft.artefactos.domain.observacion.model.ExistenciaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionExisteRule;

public class ObservacionExisteRuleImpl implements ObservacionExisteRule {

    @Override
    public void validar(ExistenciaObservacion entrada) {
        if (!entrada.existe()) {
            throw new ObservacionNoEncontradaException(entrada.observacion());
        }
    }
}
