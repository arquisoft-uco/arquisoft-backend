package com.arquisoft.artefactos.domain.revisionasesor.rules.impl;

import com.arquisoft.artefactos.domain.revisionasesor.exception.VersionPosteriorARevisionNoExisteException;
import com.arquisoft.artefactos.domain.revisionasesor.model.VersionPosteriorRevision;
import com.arquisoft.artefactos.domain.revisionasesor.rules.VersionPosteriorARevisionExisteRule;

public class VersionPosteriorARevisionExisteRuleImpl implements VersionPosteriorARevisionExisteRule {

    @Override
    public void validar(VersionPosteriorRevision entrada) {
        if (!entrada.existeVersionPosterior()) {
            throw new VersionPosteriorARevisionNoExisteException(entrada.revisionAsesor());
        }
    }
}
