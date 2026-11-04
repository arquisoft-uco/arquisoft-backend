package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoOmitidoException;
import com.arquisoft.artefactos.domain.artefacto.model.ActividadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoNoOmitidoRule;

public class ArtefactoNoOmitidoRuleImpl implements ArtefactoNoOmitidoRule {

    @Override
    public void validar(ActividadArtefacto actividad) {
        if (actividad.artefacto().estaOmitido()) {
            throw new ArtefactoOmitidoException(actividad.artefacto().getId());
        }
    }
}
