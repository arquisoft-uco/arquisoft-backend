package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorSinObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorConObservacionesRule;

public class RevisionAsesorConObservacionesRuleImpl implements RevisionAsesorConObservacionesRule {

    @Override
    public void validar(ObservacionesDeRevision entrada) {
        if (!entrada.existen()) {
            throw new RevisionAsesorSinObservacionesException(entrada.revisionAsesor());
        }
    }
}
