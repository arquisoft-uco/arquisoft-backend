package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.DisponibilidadEvaluacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.DisponibilidadEvaluacionEntregablePermitidaRule;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

public class DisponibilidadEvaluacionEntregablePermitidaRuleImpl
        implements DisponibilidadEvaluacionEntregablePermitidaRule {

    @Override
    public void validar(TransicionEstadoEntregable transicion) {
        var habilita = transicion.nuevo() == EstadoEntregable.DISPONIBLE_EVALUACION;
        if (habilita && transicion.actual() != EstadoEntregable.NUEVO) {
            throw new DisponibilidadEvaluacionEntregableNoPermitidaException(
                    transicion.entregableProyectoGrado(), transicion.actual());
        }
    }
}
