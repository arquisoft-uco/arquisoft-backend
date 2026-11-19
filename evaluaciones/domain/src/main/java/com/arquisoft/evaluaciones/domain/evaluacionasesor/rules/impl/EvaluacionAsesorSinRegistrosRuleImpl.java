package com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacionasesor.exception.EvaluacionAsesorConEvaluacionesException;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.rules.EvaluacionAsesorSinRegistrosRule;
import com.arquisoft.shared.util.UtilUUID;

public class EvaluacionAsesorSinRegistrosRuleImpl implements EvaluacionAsesorSinRegistrosRule {

    @Override
    public void validar(CambioAsesorEvaluacion cambio) {
        var tieneAsesorActual = !UtilUUID.esPorDefecto(cambio.asesorActual());
        var cambiaDeAsesor = !cambio.asesorActual().equals(cambio.asesorNuevo());
        if (tieneAsesorActual && cambiaDeAsesor && cambio.conRegistros()) {
            throw new EvaluacionAsesorConEvaluacionesException(cambio.evaluacion(), cambio.asesorActual());
        }
    }
}
