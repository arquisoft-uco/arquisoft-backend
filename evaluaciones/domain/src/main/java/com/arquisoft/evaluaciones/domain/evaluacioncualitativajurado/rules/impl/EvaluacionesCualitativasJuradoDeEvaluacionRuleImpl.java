package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionesCualitativasJuradoDeEvaluacionRule;

import java.util.HashSet;

public class EvaluacionesCualitativasJuradoDeEvaluacionRuleImpl implements EvaluacionesCualitativasJuradoDeEvaluacionRule {

    @Override
    public void validar(ExistenciaEvaluacionesCualitativasJurado existencia) {
        var faltantes = new HashSet<>(existencia.evaluacionesSolicitadas());
        faltantes.removeAll(existencia.evaluacionesEncontradas());

        if (!faltantes.isEmpty()) {
            throw new EvaluacionesCualitativasJuradoNoEncontradasException(faltantes, existencia.evaluacionJurado());
        }
    }
}
