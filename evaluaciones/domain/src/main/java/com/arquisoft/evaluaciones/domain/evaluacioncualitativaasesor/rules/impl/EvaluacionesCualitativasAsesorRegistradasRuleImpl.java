package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionesCualitativasAsesorNoRegistradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionesCualitativasAsesorRegistradasRule;

import java.util.HashSet;

public class EvaluacionesCualitativasAsesorRegistradasRuleImpl
        implements EvaluacionesCualitativasAsesorRegistradasRule {

    @Override
    public void validar(ExistenciaEvaluacionesCualitativasAsesorRegistradas entrada) {
        var faltantes = new HashSet<>(entrada.itemsSolicitados());
        faltantes.removeAll(entrada.itemsRegistrados());

        if (!faltantes.isEmpty()) {
            throw new EvaluacionesCualitativasAsesorNoRegistradasException(entrada.evaluacionAsesor(), faltantes);
        }
    }
}
