package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CriteriosCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.CriteriosCualitativosAsesorExistentesRule;

import java.util.HashSet;

public class CriteriosCualitativosAsesorExistentesRuleImpl implements CriteriosCualitativosAsesorExistentesRule {

    @Override
    public void validar(ExistenciaCriteriosCualitativosAsesor entrada) {
        var faltantes = new HashSet<>(entrada.solicitados());
        faltantes.removeAll(entrada.existentes());

        if (!faltantes.isEmpty()) {
            throw new CriteriosCualitativosAsesorNoEncontradosException(faltantes);
        }
    }
}
