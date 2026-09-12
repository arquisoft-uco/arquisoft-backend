package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.CriteriosCualitativosJuradoNoEncontradosException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaCriteriosCualitativosJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.CriteriosCualitativosJuradoExistentesRule;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class CriteriosCualitativosJuradoExistentesRuleImpl implements CriteriosCualitativosJuradoExistentesRule {

    @Override
    public void validar(ExistenciaCriteriosCualitativosJurado existencia) {
        Set<UUID> faltantes = new HashSet<>(existencia.solicitados());
        faltantes.removeAll(existencia.existentes());

        if (!faltantes.isEmpty()) {
            throw new CriteriosCualitativosJuradoNoEncontradosException(faltantes);
        }
    }
}
