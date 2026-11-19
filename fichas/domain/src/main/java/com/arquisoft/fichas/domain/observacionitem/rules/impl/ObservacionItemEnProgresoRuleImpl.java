package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.estadoobservacionrevision.EstadoObservacionRevision;
import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.observacionitem.model.EstadoObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemEnProgresoRule;

public class ObservacionItemEnProgresoRuleImpl implements ObservacionItemEnProgresoRule {

    @Override
    public void validar(EstadoObservacionItem estado) {
        if (!EstadoObservacionRevision.EN_PROGRESO.getId().equals(estado.estadoObservacionId())) {
            throw new ObservacionItemNoEnProgresoException(estado.observacionItem(), estado.estadoObservacionId());
        }
    }
}
