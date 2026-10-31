package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteDecisionObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteDecisionObservacionRule;

public class RevisionAsesorAdmiteDecisionObservacionRuleImpl implements RevisionAsesorAdmiteDecisionObservacionRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.EN_PROGRESO
                && entrada.estado() != EstadoRevisionAsesor.RESUELTA) {
            throw new RevisionAsesorNoAdmiteDecisionObservacionException(
                    entrada.revisionAsesor(), entrada.estado());
        }
    }
}
