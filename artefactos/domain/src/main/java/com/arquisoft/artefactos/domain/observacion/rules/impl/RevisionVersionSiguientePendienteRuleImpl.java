package com.arquisoft.artefactos.domain.observacion.rules.impl;

import com.arquisoft.artefactos.domain.observacion.model.SituacionReaperturaObservacion;
import com.arquisoft.artefactos.domain.observacion.rules.RevisionVersionSiguientePendienteRule;
import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionSiguienteEnviadaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;

public class RevisionVersionSiguientePendienteRuleImpl implements RevisionVersionSiguientePendienteRule {

    @Override
    public void validar(SituacionReaperturaObservacion entrada) {
        if (entrada.esReapertura()
                && !entrada.revisionVersionSiguiente().tieneEstado(EstadoRevisionAsesor.PENDIENTE)) {
            throw new RevisionVersionSiguienteEnviadaException(
                    entrada.revisionVersionSiguiente().getId(), entrada.revisionVersionSiguiente().getEstado());
        }
    }
}
