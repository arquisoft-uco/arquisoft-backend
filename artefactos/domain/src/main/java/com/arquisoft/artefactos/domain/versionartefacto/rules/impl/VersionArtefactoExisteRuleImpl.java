package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoExisteRule;

public class VersionArtefactoExisteRuleImpl implements VersionArtefactoExisteRule {

    @Override
    public void validar(ExistenciaVersionArtefacto entrada) {
        if (entrada.version().esVacio()) {
            throw new VersionArtefactoNoEncontradaException(entrada.versionArtefacto());
        }
    }
}
