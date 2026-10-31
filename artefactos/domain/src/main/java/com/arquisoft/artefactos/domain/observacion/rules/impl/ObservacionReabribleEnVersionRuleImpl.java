package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoReabribleException;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionReabribleEnVersionRule;

public class ObservacionReabribleEnVersionRuleImpl implements ObservacionReabribleEnVersionRule {

    private static final int EN_LA_VERSION_ANTERIOR = 1;

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (!entrada.esReapertura() || entrada.diferenciaVersiones() == EN_LA_VERSION_ANTERIOR) {
            return;
        }
        throw new ObservacionNoReabribleException(
                entrada.observacion(), entrada.numeroVersion(), entrada.numeroUltimaVersion());
    }
}
