package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorConObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesDeRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorSinObservacionesRule;

public class RevisionAsesorSinObservacionesRuleImpl implements RevisionAsesorSinObservacionesRule {

    @Override
    public void validar(ObservacionesDeRevision entrada) {
        if (entrada.existen()) {
            throw new RevisionAsesorConObservacionesException(entrada.revisionAsesor());
        }
    }
}
