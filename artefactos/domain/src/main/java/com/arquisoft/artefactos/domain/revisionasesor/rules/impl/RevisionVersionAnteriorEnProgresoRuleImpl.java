package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoEnProgresoException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionVersionAnteriorEnProgresoRule;

public class RevisionVersionAnteriorEnProgresoRuleImpl implements RevisionVersionAnteriorEnProgresoRule {

    @Override
    public void validar(RevisionVersionAnterior entrada) {
        if (entrada.existeVersionAnterior() && !entrada.revision().tieneEstado(EstadoRevisionAsesor.EN_PROGRESO)) {
            throw new RevisionVersionAnteriorNoEnProgresoException(entrada.numeroVersion(), entrada.revision());
        }
    }
}
