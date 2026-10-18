package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.DocumentoNoExisteException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaDocumento;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.DocumentoPreviamenteCreadoRule;

public class DocumentoPreviamenteCreadoRuleImpl implements DocumentoPreviamenteCreadoRule {

    @Override
    public void validar(ExistenciaDocumento existencia) {
        if (!existencia.nuevoDocumento() && existencia.ultimoNumeroVersion() == 0) {
            throw new DocumentoNoExisteException(existencia.nombreDocumento());
        }
    }
}
