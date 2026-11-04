package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoEliminableException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorEliminableRule;

public class RevisionAsesorEliminableRuleImpl implements RevisionAsesorEliminableRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.PENDIENTE) {
            throw new RevisionAsesorNoEliminableException(entrada.revisionAsesor(), entrada.estado());
        }
    }
}
