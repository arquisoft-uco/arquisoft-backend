package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionesCuantitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.DisponibilidadEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionesCuantitativasJuradoUnicasRule;

import java.util.HashSet;

public class EvaluacionesCuantitativasJuradoUnicasRuleImpl implements EvaluacionesCuantitativasJuradoUnicasRule {

    @Override
    public void validar(DisponibilidadEvaluacionesCuantitativasJurado entrada) {
        var duplicados = new HashSet<>(entrada.itemsSolicitados());
        duplicados.retainAll(entrada.itemsRegistrados());

        if (!duplicados.isEmpty()) {
            throw new EvaluacionesCuantitativasJuradoDuplicadasException(duplicados);
        }
    }
}
