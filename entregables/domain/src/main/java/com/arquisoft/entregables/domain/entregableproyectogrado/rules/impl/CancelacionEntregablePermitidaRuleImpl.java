package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CancelacionEntregableNoPermitidaException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.CancelacionEntregablePermitidaRule;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

public class CancelacionEntregablePermitidaRuleImpl implements CancelacionEntregablePermitidaRule {

    @Override
    public void validar(TransicionEstadoEntregable transicion) {
        var actual = transicion.actual();
        var cancela = transicion.nuevo() == EstadoEntregable.CANCELADO;
        if (cancela && actual != EstadoEntregable.DISPONIBLE_EVALUACION) {
            throw new CancelacionEntregableNoPermitidaException(transicion.entregableProyectoGrado(), actual);
        }
    }
}
