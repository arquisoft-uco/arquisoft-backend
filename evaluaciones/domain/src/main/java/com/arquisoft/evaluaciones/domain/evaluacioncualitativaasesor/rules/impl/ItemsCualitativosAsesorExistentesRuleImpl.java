package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.ItemsCualitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaItemsCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.ItemsCualitativosAsesorExistentesRule;

import java.util.HashSet;

public class ItemsCualitativosAsesorExistentesRuleImpl implements ItemsCualitativosAsesorExistentesRule {

    @Override
    public void validar(ExistenciaItemsCualitativosAsesor entrada) {
        var faltantes = new HashSet<>(entrada.solicitados());
        faltantes.removeAll(entrada.existentes());

        if (!faltantes.isEmpty()) {
            throw new ItemsCualitativosAsesorNoEncontradosException(faltantes);
        }
    }
}
