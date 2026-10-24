package com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.exception.VersionRepositorioArtefactoNoEncontradaException;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.model.ExistenciaVersionesRepositorioArtefacto;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.rules.VersionesRepositorioArtefactoExistenRule;

import java.util.HashSet;

public class VersionesRepositorioArtefactoExistenRuleImpl implements VersionesRepositorioArtefactoExistenRule {

    @Override
    public void validar(ExistenciaVersionesRepositorioArtefacto existencia) {
        var encontradas = new HashSet<>(existencia.encontradas().stream()
                .map(VersionRepositorioArtefactoDomain::getId)
                .toList());
        var faltantes = existencia.solicitadas().stream()
                .filter(solicitada -> !encontradas.contains(solicitada))
                .toList();
        if (!faltantes.isEmpty()) {
            throw new VersionRepositorioArtefactoNoEncontradaException(faltantes);
        }
    }
}
