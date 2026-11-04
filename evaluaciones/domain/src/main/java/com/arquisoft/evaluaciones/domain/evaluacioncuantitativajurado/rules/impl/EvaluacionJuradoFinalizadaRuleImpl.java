package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionJuradoFinalizadaRule;

public class EvaluacionJuradoFinalizadaRuleImpl implements EvaluacionJuradoFinalizadaRule {

    @Override
    public void validar(EstadoEvaluacionJurado estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new EvaluacionJuradoFinalizadaException(estado.evaluacionCuantitativaJurado());
        }
    }
}
