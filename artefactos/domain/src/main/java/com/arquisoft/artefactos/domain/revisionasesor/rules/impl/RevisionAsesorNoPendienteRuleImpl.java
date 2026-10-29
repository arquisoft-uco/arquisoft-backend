package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorPendienteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorNoPendienteRule;

public class RevisionAsesorNoPendienteRuleImpl implements RevisionAsesorNoPendienteRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() == EstadoRevisionAsesor.PENDIENTE) {
            throw new RevisionAsesorPendienteException(entrada.revisionAsesor());
        }
    }
}
