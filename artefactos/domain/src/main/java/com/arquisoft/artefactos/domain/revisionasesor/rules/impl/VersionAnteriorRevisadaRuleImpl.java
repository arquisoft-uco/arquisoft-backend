package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionAnteriorSinRevisionException;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.revisionasesor.rules.VersionAnteriorRevisadaRule;

public class VersionAnteriorRevisadaRuleImpl implements VersionAnteriorRevisadaRule {

    @Override
    public void validar(RevisionVersionAnterior entrada) {
        if (entrada.existeVersionAnterior() && entrada.revision().esVacio()) {
            throw new VersionAnteriorSinRevisionException(entrada.numeroVersion());
        }
    }
}
