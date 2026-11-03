package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoEnUsoException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.UsoVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoSinUsoRule;

public class VersionRepositorioArtefactoSinUsoRuleImpl implements VersionRepositorioArtefactoSinUsoRule {

    @Override
    public void validar(UsoVersionRepositorioArtefacto uso) {
        if (uso.enUso()) {
            var version = uso.version();
            throw new VersionRepositorioArtefactoEnUsoException(
                    version.getNombreDocumento(), version.getVersionDocumento(), version.getId());
        }
    }
}
