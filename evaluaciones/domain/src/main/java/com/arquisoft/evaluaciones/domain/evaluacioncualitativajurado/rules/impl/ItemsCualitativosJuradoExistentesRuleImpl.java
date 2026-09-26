package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.ItemsCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaItemsCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.ItemsCualitativosJuradoExistentesRule;

import java.util.HashSet;

public class ItemsCualitativosJuradoExistentesRuleImpl implements ItemsCualitativosJuradoExistentesRule {

    @Override
    public void validar(ExistenciaItemsCualitativosJurado existencia) {
        var faltantes = new HashSet<>(existencia.solicitados());
        faltantes.removeAll(existencia.existentes());

        if (!faltantes.isEmpty()) {
            throw new ItemsCualitativosJuradoNoEncontradosException(faltantes);
        }
    }
}
