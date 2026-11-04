package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoNoEncontradoException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.ExistenciaRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoExistenteRule;

public class RepositorioArtefactoExistenteRuleImpl implements RepositorioArtefactoExistenteRule {

    @Override
    public void validar(ExistenciaRepositorioArtefacto existencia) {
        if (existencia.encontrado().esVacio()) {
            throw new RepositorioArtefactoNoEncontradoException(existencia.repositorio());
        }
    }
}
