package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.entregables.domain.paqueteentregable.model.ExistenciaPaqueteResultadoEvaluacion;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteResultadoEvaluacionDisponibleRule;

public class PaqueteResultadoEvaluacionDisponibleRuleImpl implements PaqueteResultadoEvaluacionDisponibleRule {

    @Override
    public void validar(ExistenciaPaqueteResultadoEvaluacion entrada) {
        if (entrada.requerido() && !entrada.existe()) {
            throw new PaqueteEntregableNoDisponibleException(entrada.entregableProyectoGrado());
        }
    }
}
