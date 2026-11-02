package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorConObservacionesAbiertasException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesAbiertasRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorSinObservacionesAbiertasRule;

public class RevisionAsesorSinObservacionesAbiertasRuleImpl implements RevisionAsesorSinObservacionesAbiertasRule {

    @Override
    public void validar(ObservacionesAbiertasRevision entrada) {
        if (entrada.estadoActual() != EstadoRevisionAsesor.RESUELTA && entrada.existen()) {
            throw new RevisionAsesorConObservacionesAbiertasException(entrada.revisionAsesor());
        }
    }
}
