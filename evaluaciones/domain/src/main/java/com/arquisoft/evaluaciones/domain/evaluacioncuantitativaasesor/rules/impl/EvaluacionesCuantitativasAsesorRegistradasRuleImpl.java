package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.EvaluacionesCuantitativasAsesorNoRegistradasException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionesCuantitativasAsesorRegistradas;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionesCuantitativasAsesorRegistradasRule;

import java.util.HashSet;

public class EvaluacionesCuantitativasAsesorRegistradasRuleImpl
        implements EvaluacionesCuantitativasAsesorRegistradasRule {

    @Override
    public void validar(ExistenciaEvaluacionesCuantitativasAsesorRegistradas entrada) {
        var faltantes = new HashSet<>(entrada.itemsSolicitados());
        faltantes.removeAll(entrada.itemsRegistrados());

        if (!faltantes.isEmpty()) {
            throw new EvaluacionesCuantitativasAsesorNoRegistradasException(entrada.evaluacionAsesor(), faltantes);
        }
    }
}
