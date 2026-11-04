package com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.exception.OmisionEvaluacionJuradoFinalizadaException;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.rules.EvaluacionJuradoAdmiteOmisionRule;

public class EvaluacionJuradoAdmiteOmisionRuleImpl implements EvaluacionJuradoAdmiteOmisionRule {

    @Override
    public void validar(EstadoOmisionEvaluacionesCuantitativasJurado estado) {
        if (estado.estado() == EstadoEvaluacion.FINALIZADA) {
            throw new OmisionEvaluacionJuradoFinalizadaException(estado.evaluacionJurado());
        }
    }
}
