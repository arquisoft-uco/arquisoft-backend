package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEncontradaException;
import com.arquisoft.fichas.domain.observacionitem.model.ExistenciaObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemExisteRule;

public class ObservacionItemExisteRuleImpl implements ObservacionItemExisteRule {

    @Override
    public void validar(ExistenciaObservacionItem existencia) {
        if (!existencia.existe()) {
            throw new ObservacionItemNoEncontradaException(existencia.observacionItem());
        }
    }
}
