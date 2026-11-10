package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionVersionAnteriorNoResueltaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionVersionAnteriorResueltaRule;

public class RevisionVersionAnteriorResueltaRuleImpl implements RevisionVersionAnteriorResueltaRule {

    @Override
    public void validar(RevisionVersionAnterior entrada) {
        if (entrada.existeVersionAnterior() && !entrada.revision().tieneEstado(EstadoRevisionAsesor.RESUELTA)) {
            throw new RevisionVersionAnteriorNoResueltaException(entrada.numeroVersion(), entrada.revision());
        }
    }
}
