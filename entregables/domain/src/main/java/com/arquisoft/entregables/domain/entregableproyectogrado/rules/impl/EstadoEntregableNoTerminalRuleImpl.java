package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoEntregableTerminalException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EstadoEntregableNoTerminalRule;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

public class EstadoEntregableNoTerminalRuleImpl implements EstadoEntregableNoTerminalRule {

    @Override
    public void validar(TransicionEstadoEntregable transicion) {
        var actual = transicion.actual();
        if (actual == EstadoEntregable.CERRADO || actual == EstadoEntregable.CANCELADO) {
            throw new EstadoEntregableTerminalException(transicion.entregableProyectoGrado(), actual);
        }
    }
}
