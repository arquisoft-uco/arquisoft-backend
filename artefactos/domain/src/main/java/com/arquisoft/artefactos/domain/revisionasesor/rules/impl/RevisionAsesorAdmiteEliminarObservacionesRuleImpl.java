package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.RevisionAsesorNoAdmiteEliminarObservacionesException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoActualRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.rules.RevisionAsesorAdmiteEliminarObservacionesRule;

public class RevisionAsesorAdmiteEliminarObservacionesRuleImpl
        implements RevisionAsesorAdmiteEliminarObservacionesRule {

    @Override
    public void validar(EstadoActualRevisionAsesor entrada) {
        if (entrada.estado() != EstadoRevisionAsesor.PENDIENTE) {
            throw new RevisionAsesorNoAdmiteEliminarObservacionesException(
                    entrada.revisionAsesor(), entrada.estado());
        }
    }
}
