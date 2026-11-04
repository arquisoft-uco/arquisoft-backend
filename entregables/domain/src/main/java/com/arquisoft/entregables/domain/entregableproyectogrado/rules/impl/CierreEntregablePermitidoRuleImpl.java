package com.arquisoft.entregables.domain.entregableproyectogrado.rules.impl;

import com.arquisoft.entregables.domain.entregableproyectogrado.exception.CierreEntregableNoPermitidoException;
import com.arquisoft.entregables.domain.entregableproyectogrado.model.TransicionEstadoEntregable;
import com.arquisoft.entregables.domain.entregableproyectogrado.rules.CierreEntregablePermitidoRule;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;

public class CierreEntregablePermitidoRuleImpl implements CierreEntregablePermitidoRule {

    @Override
    public void validar(TransicionEstadoEntregable transicion) {
        var actual = transicion.actual();
        var cierra = transicion.nuevo() == EstadoEntregable.CERRADO;
        var cerrable = actual == EstadoEntregable.APROBADO || actual == EstadoEntregable.APROBADO_CON_OBSERVACIONES;
        if (cierra && !cerrable) {
            throw new CierreEntregableNoPermitidoException(transicion.entregableProyectoGrado(), actual);
        }
    }
}
