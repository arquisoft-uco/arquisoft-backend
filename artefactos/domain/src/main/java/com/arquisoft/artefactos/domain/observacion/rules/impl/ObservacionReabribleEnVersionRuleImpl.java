package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.exception.ObservacionNoReabribleException;
import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.ObservacionReabribleEnVersionRule;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;

public class ObservacionReabribleEnVersionRuleImpl implements ObservacionReabribleEnVersionRule {

    private static final int EN_LA_ULTIMA_VERSION = 0;
    private static final int EN_LA_VERSION_ANTERIOR = 1;

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (!entrada.esReapertura()) {
            return;
        }
        var diferencia = entrada.diferenciaVersiones();
        var enUltimaVersionEnProgreso = diferencia == EN_LA_ULTIMA_VERSION
                && entrada.estadoRevision() == EstadoRevisionAsesor.EN_PROGRESO;
        if (enUltimaVersionEnProgreso || diferencia == EN_LA_VERSION_ANTERIOR) {
            return;
        }
        throw new ObservacionNoReabribleException(
                entrada.observacion(), entrada.numeroVersion(), entrada.numeroUltimaVersion(),
                entrada.estadoRevision());
    }
}
