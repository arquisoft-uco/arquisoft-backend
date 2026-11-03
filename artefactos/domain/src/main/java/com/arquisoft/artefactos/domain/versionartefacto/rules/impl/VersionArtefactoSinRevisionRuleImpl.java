package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoConRevisionException;
import com.arquisoft.artefactos.domain.versionartefacto.model.RevisionesVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoSinRevisionRule;

public class VersionArtefactoSinRevisionRuleImpl implements VersionArtefactoSinRevisionRule {

    @Override
    public void validar(RevisionesVersionArtefacto entrada) {
        if (entrada.tieneRevision()) {
            throw new VersionArtefactoConRevisionException(entrada.versionArtefacto(), entrada.numeroVersion());
        }
    }
}
