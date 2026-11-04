package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.ItemsCuantitativosAsesorNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaItemsCuantitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.ItemsCuantitativosAsesorExistentesRule;

import java.util.HashSet;

public class ItemsCuantitativosAsesorExistentesRuleImpl implements ItemsCuantitativosAsesorExistentesRule {

    @Override
    public void validar(ExistenciaItemsCuantitativosAsesor entrada) {
        var faltantes = new HashSet<>(entrada.solicitados());
        faltantes.removeAll(entrada.valoresMaximos().keySet());

        if (!faltantes.isEmpty()) {
            throw new ItemsCuantitativosAsesorNoEncontradosException(faltantes);
        }
    }
}
