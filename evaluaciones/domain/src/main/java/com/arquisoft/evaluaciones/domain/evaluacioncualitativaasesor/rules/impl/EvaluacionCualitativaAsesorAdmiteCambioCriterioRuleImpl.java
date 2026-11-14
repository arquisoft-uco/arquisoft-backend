package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.CambioCriterioEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorAdmiteCambioCriterioRule;

public class EvaluacionCualitativaAsesorAdmiteCambioCriterioRuleImpl
        implements EvaluacionCualitativaAsesorAdmiteCambioCriterioRule {

    @Override
    public void validar(EstadoCambioCriterioEvaluacionCualitativaAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new CambioCriterioEvaluacionCualitativaAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
