package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteAvanceObservacionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteAvanceObservacionRule;

public class RevisionAsesorAdmiteAvanceObservacionRuleImpl implements RevisionAsesorAdmiteAvanceObservacionRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.EN_PROGRESO) {
            throw new RevisionAsesorNoAdmiteAvanceObservacionException(entrada.revisionAsesor(), entrada.estado());
        }
    }
}
