package com.arquisoft.fichas.domain.observacionevaluacion.rules.impl;

import com.arquisoft.fichas.domain.observacionevaluacion.exception.ObservacionEvaluacionDuplicadaException;
import com.arquisoft.fichas.domain.observacionevaluacion.model.DisponibilidadObservacionEvaluacion;
import com.arquisoft.fichas.domain.observacionevaluacion.rules.ObservacionEvaluacionNoDuplicadaRule;

public class ObservacionEvaluacionNoDuplicadaRuleImpl implements ObservacionEvaluacionNoDuplicadaRule {

    @Override
    public void validar(DisponibilidadObservacionEvaluacion disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new ObservacionEvaluacionDuplicadaException(
                    disponibilidad.evaluacionFichaPerfil(), disponibilidad.observacion());
        }
    }
}
