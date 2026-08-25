package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEnEvaluacionNoManualException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.SolicitudEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEnEvaluacionNoManualRule;

public class EstadoEnEvaluacionNoManualRuleImpl implements EstadoEnEvaluacionNoManualRule {

    @Override
    public void validar(SolicitudEstadoEvaluacion solicitud) {
        if (solicitud.estadoEvaluacion().esEnEvaluacion()) {
            throw new EstadoEnEvaluacionNoManualException();
        }
    }
}
