package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.ItemsCuantitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaItemsCuantitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.ItemsCuantitativosJuradoExistentesRule;

import java.util.HashSet;

public class ItemsCuantitativosJuradoExistentesRuleImpl implements ItemsCuantitativosJuradoExistentesRule {

    @Override
    public void validar(ExistenciaItemsCuantitativosJurado entrada) {
        var faltantes = new HashSet<>(entrada.solicitados());
        faltantes.removeAll(entrada.valoresMaximos().keySet());

        if (!faltantes.isEmpty()) {
            throw new ItemsCuantitativosJuradoNoEncontradosException(faltantes);
        }
    }
}
