package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.VersionRepositorioOtroDocumentoException;
import com.arquisoft.artefactos.domain.artefacto.model.CambioVersionRepositorio;
import com.arquisoft.artefactos.domain.artefacto.rules.VersionRepositorioMismoDocumentoRule;

public class VersionRepositorioMismoDocumentoRuleImpl implements VersionRepositorioMismoDocumentoRule {

    @Override
    public void validar(CambioVersionRepositorio cambio) {
        var actual = cambio.actual();
        var elegida = cambio.elegida();
        if (!actual.getRepositorioArtefacto().equals(elegida.getRepositorioArtefacto())
                || !actual.getNombreDocumento().equals(elegida.getNombreDocumento())) {
            throw new VersionRepositorioOtroDocumentoException(
                    actual.getNombreDocumento(), actual.getNombreRepositorio(), elegida.getId(), actual.getId());
        }
    }
}
