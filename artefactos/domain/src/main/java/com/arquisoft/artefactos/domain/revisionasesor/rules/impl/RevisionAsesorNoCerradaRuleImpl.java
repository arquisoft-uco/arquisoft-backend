package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorCerradaException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorNoCerradaRule;

public class RevisionAsesorNoCerradaRuleImpl implements RevisionAsesorNoCerradaRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() == EstadoRevisionAsesor.CERRADA) {
            throw new RevisionAsesorCerradaException(entrada.revisionAsesor());
        }
    }
}
