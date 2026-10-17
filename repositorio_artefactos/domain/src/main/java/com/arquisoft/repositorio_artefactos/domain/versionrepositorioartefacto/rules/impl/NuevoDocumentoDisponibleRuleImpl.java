package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoYaExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.NuevoDocumentoDisponibleRule;

public class NuevoDocumentoDisponibleRuleImpl implements NuevoDocumentoDisponibleRule {

    @Override
    public void validar(ExistenciaDocumento existencia) {
        if (existencia.nuevoDocumento() && existencia.ultimoNumeroVersion() > 0) {
            throw new DocumentoYaExisteException(existencia.nombreDocumento());
        }
    }
}
