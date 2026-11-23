package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoDescartadaNoAsignableException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionAprobadaConObservacionesException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EvaluacionSinObservacionesException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.AsignacionEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionAsignableRule;

public class EstadoEvaluacionAsignableRuleImpl implements EstadoEvaluacionAsignableRule {

    @Override
    public void validar(AsignacionEstadoEvaluacion asignacion) {
        var estado = asignacion.estadoEvaluacion();
        var tieneObservaciones = asignacion.observaciones() > 0;

        if (estado == EstadoEvaluacion.DESCARTADA) {
            throw new EstadoDescartadaNoAsignableException(asignacion.evaluacionFichaPerfil());
        }

        if (estado == EstadoEvaluacion.APROBADA && tieneObservaciones) {
            throw new EvaluacionAprobadaConObservacionesException(
                    asignacion.evaluacionFichaPerfil(), asignacion.observaciones());
        }

        if (estado == EstadoEvaluacion.APROBADA_CON_OBSERVACIONES && !tieneObservaciones) {
            throw new EvaluacionSinObservacionesException(asignacion.evaluacionFichaPerfil());
        }
    }
}
