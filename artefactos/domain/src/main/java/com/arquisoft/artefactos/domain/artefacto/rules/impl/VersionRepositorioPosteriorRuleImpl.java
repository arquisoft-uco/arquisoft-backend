package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioNoPosteriorException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioPosteriorRule;

public class VersionRepositorioPosteriorRuleImpl implements VersionRepositorioPosteriorRule {

    @Override
    public void validar(CambioVersionRepositorio cambio) {
        if (cambio.elegida().getVersionDocumento() <= cambio.actual().getVersionDocumento()) {
            throw new VersionRepositorioNoPosteriorException(
                    cambio.elegida().getNombreDocumento(),
                    cambio.elegida().getVersionDocumento(),
                    cambio.actual().getVersionDocumento(),
                    cambio.elegida().getId(),
                    cambio.actual().getId());
        }
    }
}
