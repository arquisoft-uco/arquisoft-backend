package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.TamanoArchivoPermitidoRule;

public class TamanoArchivoPermitidoRuleImpl implements TamanoArchivoPermitidoRule {

    @Override
    public void validar(LimiteTamanoArchivo limite) {
        if (limite.tamanoBytes() <= 0 || limite.tamanoBytes() > limite.maximoBytes()) {
            throw new TamanoArchivoExcedidoException(limite.tamanoBytes(), limite.maximoBytes());
        }
    }
}
