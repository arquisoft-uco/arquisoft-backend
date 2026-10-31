package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.VersionArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaVersionArtefactoConsultada;
import com.arquisoft.artefactos.domain.versionartefacto.rules.VersionArtefactoConsultadaExisteRule;
import com.arquisoft.shared.util.UtilUUID;

public class VersionArtefactoConsultadaExisteRuleImpl implements VersionArtefactoConsultadaExisteRule {

    @Override
    public void validar(ExistenciaVersionArtefactoConsultada entrada) {
        if (UtilUUID.esPorDefecto(entrada.asesor())) {
            throw new VersionArtefactoNoEncontradaException(entrada.versionArtefacto());
        }
    }
}
