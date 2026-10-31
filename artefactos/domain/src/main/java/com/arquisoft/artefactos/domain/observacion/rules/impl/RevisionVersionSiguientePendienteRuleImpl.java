package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguientePendienteRule;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteEnviadaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;

public class RevisionVersionSiguientePendienteRuleImpl implements RevisionVersionSiguientePendienteRule {

    private static final int EN_LA_VERSION_ANTERIOR = 1;

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (entrada.esReapertura()
                && entrada.diferenciaVersiones() == EN_LA_VERSION_ANTERIOR
                && !entrada.revisionVersionSiguiente().tieneEstado(EstadoRevisionAsesor.PENDIENTE)) {
            throw new RevisionVersionSiguienteEnviadaException(
                    entrada.revisionVersionSiguiente().getId(), entrada.revisionVersionSiguiente().getEstado());
        }
    }
}
