package com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.exception.EvaluacionAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.rules.EvaluacionAsesorExistenteRule;
import com.arquisoft.shared.util.UtilUUID;

public class EvaluacionAsesorExistenteRuleImpl implements EvaluacionAsesorExistenteRule {

    @Override
    public void validar(ExistenciaEvaluacionAsesor existencia) {
        if (UtilUUID.esPorDefecto(existencia.asesorAsignado())) {
            throw new EvaluacionAsesorNoEncontradaException(existencia.evaluacionAsesor());
        }
    }
}
