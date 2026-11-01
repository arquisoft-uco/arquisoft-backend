package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioOtroRepositorioException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioMismoRepositorioRule;

public class VersionRepositorioMismoRepositorioRuleImpl implements VersionRepositorioMismoRepositorioRule {

    @Override
    public void validar(CambioVersionRepositorio cambio) {
        if (!cambio.actual().getRepositorioArtefacto().equals(cambio.elegida().getRepositorioArtefacto())) {
            throw new VersionRepositorioOtroRepositorioException(
                    cambio.actual().getNombreRepositorio(), cambio.elegida().getId(), cambio.actual().getId());
        }
    }
}
