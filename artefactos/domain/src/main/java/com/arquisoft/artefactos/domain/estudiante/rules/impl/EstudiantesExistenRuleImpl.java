package com.arquisoft.artefactos.domain.estudiante.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudiantesNoEncontradosException;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesExistenRule;

import java.util.HashSet;

public class EstudiantesExistenRuleImpl implements EstudiantesExistenRule {

    @Override
    public void validar(ExistenciaEstudiantes existencia) {
        var encontrados = new HashSet<>(existencia.encontrados().stream()
                .map(EstudianteDomain::getId)
                .toList());
        var faltantes = existencia.solicitados().stream()
                .filter(solicitado -> !encontrados.contains(solicitado))
                .toList();
        if (!faltantes.isEmpty()) {
            throw new EstudiantesNoEncontradosException(faltantes);
        }
    }
}
