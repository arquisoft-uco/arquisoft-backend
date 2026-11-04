package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteObservacionesRule;

public class RevisionAsesorAdmiteObservacionesRuleImpl implements RevisionAsesorAdmiteObservacionesRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.PENDIENTE) {
            throw new RevisionAsesorNoAdmiteObservacionesException(entrada.revisionAsesor(), entrada.estado());
        }
    }
}
