package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionesCuantitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.DisponibilidadEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionesCuantitativasAsesorUnicasRule;

import java.util.HashSet;

public class EvaluacionesCuantitativasAsesorUnicasRuleImpl implements EvaluacionesCuantitativasAsesorUnicasRule {

    @Override
    public void validar(DisponibilidadEvaluacionesCuantitativasAsesor entrada) {
        var duplicados = new HashSet<>(entrada.itemsSolicitados());
        duplicados.retainAll(entrada.itemsRegistrados());

        if (!duplicados.isEmpty()) {
            throw new EvaluacionesCuantitativasAsesorDuplicadasException(duplicados);
        }
    }
}
