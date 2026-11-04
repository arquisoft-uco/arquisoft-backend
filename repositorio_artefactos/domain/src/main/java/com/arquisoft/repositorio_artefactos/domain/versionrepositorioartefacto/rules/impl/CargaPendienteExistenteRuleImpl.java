package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.exception.CargaPendienteNoEncontradaException;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.ExistenciaCargaPendiente;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.rules.CargaPendienteExistenteRule;

public class CargaPendienteExistenteRuleImpl implements CargaPendienteExistenteRule {

    @Override
    public void validar(ExistenciaCargaPendiente existencia) {
        if (existencia.encontrada().esVacio()) {
            throw new CargaPendienteNoEncontradaException(existencia.carga());
        }
    }
}
