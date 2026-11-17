package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.OmisionEvaluacionCualitativaAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionCualitativaAsesorAdmiteOmisionRule;

public class EvaluacionCualitativaAsesorAdmiteOmisionRuleImpl
        implements EvaluacionCualitativaAsesorAdmiteOmisionRule {

    @Override
    public void validar(EstadoOmisionEvaluacionesCualitativasAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new OmisionEvaluacionCualitativaAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
