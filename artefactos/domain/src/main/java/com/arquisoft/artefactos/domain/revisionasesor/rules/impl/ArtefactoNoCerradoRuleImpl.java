package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.ArtefactoCerradoException;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.ArtefactoNoCerradoRule;

public class ArtefactoNoCerradoRuleImpl implements ArtefactoNoCerradoRule {

    @Override
    public void validar(RevisionVersionAnterior entrada) {
        if (entrada.existeVersionAnterior() && entrada.revision().tieneEstado(EstadoRevisionAsesor.CERRADA)) {
            throw new ArtefactoCerradoException(entrada.revision().getId());
        }
    }
}
