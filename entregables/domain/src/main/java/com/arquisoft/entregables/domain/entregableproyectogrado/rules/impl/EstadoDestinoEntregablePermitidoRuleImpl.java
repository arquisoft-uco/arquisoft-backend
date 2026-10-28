package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.EstadoDestinoEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.EstadoDestinoEntregablePermitidoRule;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

public class EstadoDestinoEntregablePermitidoRuleImpl implements EstadoDestinoEntregablePermitidoRule {

    @Override
    public void validar(TransicionEstadoEntregable transicion) {
        var nuevo = transicion.nuevo();
        if (nuevo != EstadoEntregable.CANCELADO && nuevo != EstadoEntregable.CERRADO) {
            throw new EstadoDestinoEntregableNoPermitidoException(transicion.entregableProyectoGrado(), nuevo);
        }
    }
}
