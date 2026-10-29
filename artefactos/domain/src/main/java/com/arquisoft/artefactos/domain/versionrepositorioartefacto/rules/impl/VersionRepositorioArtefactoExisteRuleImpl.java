package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExisteRule;

public class VersionRepositorioArtefactoExisteRuleImpl implements VersionRepositorioArtefactoExisteRule {

    @Override
    public void validar(ExistenciaVersionRepositorioArtefacto existencia) {
        if (existencia.encontrada().esVacio()) {
            throw new VersionRepositorioArtefactoNoEncontradaException(existencia.versionRepositorioArtefacto());
        }
    }
}
