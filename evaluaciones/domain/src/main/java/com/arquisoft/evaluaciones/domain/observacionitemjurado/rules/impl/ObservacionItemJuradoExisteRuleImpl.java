package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.ObservacionItemJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.ExistenciaObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.ObservacionItemJuradoExisteRule;

public class ObservacionItemJuradoExisteRuleImpl implements ObservacionItemJuradoExisteRule {

    @Override
    public void validar(ExistenciaObservacionItemJurado existencia) {
        if (!existencia.existe()) {
            throw new ObservacionItemJuradoNoEncontradaException(existencia.observacionItemJurado());
        }
    }
}
