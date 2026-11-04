package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoEsUltimaRule;

public class VersionArtefactoEsUltimaRuleImpl implements VersionArtefactoEsUltimaRule {

    @Override
    public void validar(VigenciaVersionArtefacto entrada) {
        if (!entrada.esUltima()) {
            throw new VersionArtefactoNoEsUltimaException(entrada.versionArtefacto(), entrada.numeroVersion());
        }
    }
}
