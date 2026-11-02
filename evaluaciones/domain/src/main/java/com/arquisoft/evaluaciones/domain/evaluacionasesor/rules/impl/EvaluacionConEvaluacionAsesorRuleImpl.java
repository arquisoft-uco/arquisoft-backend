package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionSinEvaluacionAsesorException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.ExistenciaEvaluacionAsesorDeEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EvaluacionConEvaluacionAsesorRule;
import com.arquisoft.shared.util.UtilUUID;

public class EvaluacionConEvaluacionAsesorRuleImpl implements EvaluacionConEvaluacionAsesorRule {

    @Override
    public void validar(ExistenciaEvaluacionAsesorDeEvaluacion existencia) {
        if (UtilUUID.esPorDefecto(existencia.evaluacionAsesor())) {
            throw new EvaluacionSinEvaluacionAsesorException(existencia.evaluacion());
        }
    }
}
