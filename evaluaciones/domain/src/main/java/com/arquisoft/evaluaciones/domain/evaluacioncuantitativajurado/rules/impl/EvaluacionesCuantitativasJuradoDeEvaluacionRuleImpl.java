package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoNoEncontradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoDeEvaluacionRule;

import java.util.HashSet;

public class EvaluacionesCuantitativasJuradoDeEvaluacionRuleImpl implements EvaluacionesCuantitativasJuradoDeEvaluacionRule {

    @Override
    public void validar(ExistenciaEvaluacionesCuantitativasJurado existencia) {
        var faltantes = new HashSet<>(existencia.evaluacionesSolicitadas());
        faltantes.removeAll(existencia.evaluacionesEncontradas());

        if (!faltantes.isEmpty()) {
            throw new EvaluacionesCuantitativasJuradoNoEncontradasException(faltantes, existencia.evaluacionJurado());
        }
    }
}
