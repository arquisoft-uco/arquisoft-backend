package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioIgualException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioDistintaRule;

public class VersionRepositorioDistintaRuleImpl implements VersionRepositorioDistintaRule {

    @Override
    public void validar(CambioVersionRepositorio cambio) {
        var actual = cambio.actual();
        if (cambio.elegida().getId().equals(actual.getId())) {
            throw new VersionRepositorioIgualException(
                    actual.getVersionDocumento(), actual.getNombreDocumento(), actual.getId());
        }
    }
}
