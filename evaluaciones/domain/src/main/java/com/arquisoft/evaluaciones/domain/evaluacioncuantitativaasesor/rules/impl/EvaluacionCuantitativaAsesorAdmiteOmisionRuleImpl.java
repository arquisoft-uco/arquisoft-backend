package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.OmisionEvaluacionAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoOmisionEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorAdmiteOmisionRule;

public class EvaluacionCuantitativaAsesorAdmiteOmisionRuleImpl
        implements EvaluacionCuantitativaAsesorAdmiteOmisionRule {

    @Override
    public void validar(EstadoOmisionEvaluacionesCuantitativasAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new OmisionEvaluacionAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
