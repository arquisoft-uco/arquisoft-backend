package com.arquisoft.artefactos.domain.versionartefacto.rules.impl;

import com.arquisoft.artefactos.domain.versionartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.artefactos.domain.versionartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.artefactos.domain.versionartefacto.rules.CargaPendienteExistenteRule;

public class CargaPendienteExistenteRuleImpl implements CargaPendienteExistenteRule {

    @Override
    public void validar(ExistenciaCargaPendiente existencia) {
        if (existencia.encontrada().esVacio()) {
            throw new CargaPendienteNoEncontradaException(existencia.carga());
        }
    }
}
