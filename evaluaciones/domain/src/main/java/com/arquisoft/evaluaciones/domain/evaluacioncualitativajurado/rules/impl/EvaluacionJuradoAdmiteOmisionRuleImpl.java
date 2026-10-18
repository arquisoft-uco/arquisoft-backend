package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionJuradoAdmiteOmisionRule;

public class EvaluacionJuradoAdmiteOmisionRuleImpl implements EvaluacionJuradoAdmiteOmisionRule {

    @Override
    public void validar(EstadoOmisionEvaluacionesCualitativasJurado estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new EvaluacionJuradoFinalizadaException(estado.evaluacionJurado());
        }
    }
}
