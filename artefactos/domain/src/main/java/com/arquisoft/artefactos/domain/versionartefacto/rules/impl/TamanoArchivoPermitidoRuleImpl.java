package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.TamanoArchivoExcedidoException;
import com.arquisoft.artefactos.domain.versionartefacto.model.LimiteTamanoArchivo;
import com.arquisoft.artefactos.domain.versionartefacto.rules.TamanoArchivoPermitidoRule;

public class TamanoArchivoPermitidoRuleImpl implements TamanoArchivoPermitidoRule {

    @Override
    public void validar(LimiteTamanoArchivo limite) {
        if (limite.tamanoBytes() <= 0 || limite.tamanoBytes() > limite.maximoBytes()) {
            throw new TamanoArchivoExcedidoException(limite.tamanoBytes(), limite.maximoBytes());
        }
    }
}
