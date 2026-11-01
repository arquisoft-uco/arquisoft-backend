package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoCerrableException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorCerrableRule;

public class RevisionAsesorCerrableRuleImpl implements RevisionAsesorCerrableRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.PENDIENTE) {
            throw new RevisionAsesorNoCerrableException(entrada.revisionAsesor(), entrada.estado());
        }
    }
}
