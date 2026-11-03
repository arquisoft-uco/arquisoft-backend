package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.RevisionAnteriorNoPermiteEliminarVersionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionartefacto.rules.RevisionAnteriorPermiteEliminarVersionRule;

public class RevisionAnteriorPermiteEliminarVersionRuleImpl implements RevisionAnteriorPermiteEliminarVersionRule {

    @Override
    public void validar(RevisionVersionAnterior entrada) {
        if (entrada.existeVersionAnterior() && !entrada.revision().tieneEstado(EstadoRevisionAsesor.EN_PROGRESO)) {
            throw new RevisionAnteriorNoPermiteEliminarVersionException(
                    entrada.numeroVersion() + 1, entrada.revision());
        }
    }
}
