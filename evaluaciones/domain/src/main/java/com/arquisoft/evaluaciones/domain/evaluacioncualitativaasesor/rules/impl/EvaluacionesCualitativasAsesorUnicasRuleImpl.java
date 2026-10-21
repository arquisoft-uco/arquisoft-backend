package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.DisponibilidadEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionesCualitativasAsesorUnicasRule;

import java.util.HashSet;

public class EvaluacionesCualitativasAsesorUnicasRuleImpl implements EvaluacionesCualitativasAsesorUnicasRule {

    @Override
    public void validar(DisponibilidadEvaluacionesCualitativasAsesor entrada) {
        var duplicados = new HashSet<>(entrada.itemsSolicitados());
        duplicados.retainAll(entrada.itemsRegistrados());

        if (!duplicados.isEmpty()) {
            throw new EvaluacionesCualitativasAsesorDuplicadasException(duplicados);
        }
    }
}
