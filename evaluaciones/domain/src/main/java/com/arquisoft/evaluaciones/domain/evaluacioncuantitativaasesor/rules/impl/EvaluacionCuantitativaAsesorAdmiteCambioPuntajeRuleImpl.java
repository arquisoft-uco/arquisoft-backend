package com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.exception.CambioPuntajeEvaluacionAsesorFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoCambioPuntajeEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.rules.EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRule;

public class EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRuleImpl
        implements EvaluacionCuantitativaAsesorAdmiteCambioPuntajeRule {

    @Override
    public void validar(EstadoCambioPuntajeEvaluacionCuantitativaAsesor estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new CambioPuntajeEvaluacionAsesorFinalizadaException(estado.evaluacionAsesor());
        }
    }
}
