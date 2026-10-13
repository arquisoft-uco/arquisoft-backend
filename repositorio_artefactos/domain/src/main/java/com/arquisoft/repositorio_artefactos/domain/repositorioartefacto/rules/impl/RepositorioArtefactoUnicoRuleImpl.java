package com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.impl;

import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.exception.RepositorioArtefactoDuplicadoException;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.model.DisponibilidadRepositorioArtefacto;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.rules.RepositorioArtefactoUnicoRule;

public class RepositorioArtefactoUnicoRuleImpl implements RepositorioArtefactoUnicoRule {

    @Override
    public void validar(DisponibilidadRepositorioArtefacto disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new RepositorioArtefactoDuplicadoException(disponibilidad.nombreRepositorio());
        }
    }
}
