package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoCerradaException;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemCerradaRule;

public class ObservacionItemCerradaRuleImpl implements ObservacionItemCerradaRule {

    @Override
    public void validar(EstadoObservacionItem estado) {
        if (!EstadoObservacionRevision.CERRADO.getId().equals(estado.estadoObservacionId())) {
            throw new ObservacionItemNoCerradaException(estado.observacionItem(), estado.estadoObservacionId());
        }
    }
}
