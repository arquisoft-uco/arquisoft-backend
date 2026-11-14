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
        var cancelable = actual == EstadoEntregable.NUEVO || actual == EstadoEntregable.RECHAZADO;
        if (cancela && !cancelable) {
            throw new CancelacionEntregableNoPermitidaException(transicion.entregableProyectoGrado(), actual);
        }
    }
}
