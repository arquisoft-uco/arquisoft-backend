package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.ObservacionItemAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.ExistenciaObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.ObservacionItemAsesorExisteRule;

public class ObservacionItemAsesorExisteRuleImpl implements ObservacionItemAsesorExisteRule {

    @Override
    public void validar(ExistenciaObservacionItemAsesor existencia) {
        if (!existencia.existe()) {
            throw new ObservacionItemAsesorNoEncontradaException(existencia.observacion());
        }
    }
}
