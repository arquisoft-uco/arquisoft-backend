package com.arquisoft.fichas.domain.estadoevaluacionficha.rules.impl;

import com.arquisoft.fichas.domain.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.domain.estadoevaluacionficha.model.DisponibilidadEstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.rules.EstadoEvaluacionNoDuplicadoRule;

public class EstadoEvaluacionNoDuplicadoRuleImpl implements EstadoEvaluacionNoDuplicadoRule {

    @Override
    public void validar(DisponibilidadEstadoEvaluacion disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new EstadoEvaluacionDuplicadoException(
                    disponibilidad.evaluacionFichaPerfil(), disponibilidad.estadoEvaluacion().getId());
        }
    }
}
