package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEsUltimaParaEliminarException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.VigenciaVersionRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionRepositorioArtefactoUltimaParaEliminarRule;

public class VersionRepositorioArtefactoUltimaParaEliminarRuleImpl
        implements VersionRepositorioArtefactoUltimaParaEliminarRule {

    @Override
    public void validar(VigenciaVersionRepositorioArtefacto vigencia) {
        var version = vigencia.version();
        if (version.getVersionDocumento() < vigencia.ultimaVersionDocumento()) {
            throw new VersionRepositorioArtefactoNoEsUltimaParaEliminarException(
                    version.getNombreDocumento(),
                    version.getVersionDocumento(),
                    vigencia.ultimaVersionDocumento(),
                    version.getId());
        }
    }
}
