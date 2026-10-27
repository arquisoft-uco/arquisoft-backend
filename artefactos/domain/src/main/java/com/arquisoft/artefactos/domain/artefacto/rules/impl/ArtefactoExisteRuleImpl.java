package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactoExisteRule;

public class ArtefactoExisteRuleImpl implements ArtefactoExisteRule {

    @Override
    public void validar(ExistenciaArtefacto existencia) {
        if (existencia.encontrado().esVacio()) {
            throw new ArtefactoNoEncontradoException(existencia.artefacto());
        }
    }
}
