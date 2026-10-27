package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.exception.ArtefactoNoEncontradoException;
import com.arquisoft.artefactos.domain.artefacto.model.ExistenciaArtefactos;
import com.arquisoft.artefactos.domain.artefacto.rules.ArtefactosExistenRule;

import java.util.HashSet;

public class ArtefactosExistenRuleImpl implements ArtefactosExistenRule {

    @Override
    public void validar(ExistenciaArtefactos existencia) {
        var encontrados = new HashSet<>(existencia.encontrados().stream()
                .map(ArtefactoDomain::getId)
                .toList());
        var faltante = existencia.solicitados().stream()
                .filter(solicitado -> !encontrados.contains(solicitado))
                .findFirst();
        if (faltante.isPresent()) {
            throw new ArtefactoNoEncontradoException(faltante.get());
        }
    }
}
