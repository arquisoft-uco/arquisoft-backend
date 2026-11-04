package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.artefactos.domain.versionartefacto.model.VigenciaVersionArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoUltimaParaEliminarRule;

public class VersionArtefactoUltimaParaEliminarRuleImpl implements VersionArtefactoUltimaParaEliminarRule {

    @Override
    public void validar(VigenciaVersionArtefacto entrada) {
        if (!entrada.esUltima()) {
            throw new VersionArtefactoNoEsUltimaParaEliminarException(
                    entrada.versionArtefacto(), entrada.numeroVersion());
        }
    }
}
