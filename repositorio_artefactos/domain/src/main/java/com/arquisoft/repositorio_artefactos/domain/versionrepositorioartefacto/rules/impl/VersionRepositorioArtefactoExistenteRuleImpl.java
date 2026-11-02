package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoExistenteRule;

public class VersionRepositorioArtefactoExistenteRuleImpl implements VersionRepositorioArtefactoExistenteRule {

    @Override
    public void validar(ExistenciaVersionRepositorioArtefacto existencia) {
        if (existencia.archivo().esVacio()) {
            throw new VersionRepositorioArtefactoNoEncontradaException(existencia.version());
        }
    }
}
