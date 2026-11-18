package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableCanceladoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.EstadoEntregableDelPaquete;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableNoCanceladoRule;

public class PaqueteEntregableNoCanceladoRuleImpl implements PaqueteEntregableNoCanceladoRule {

    @Override
    public void validar(EstadoEntregableDelPaquete entrada) {
        if (entrada.estadoEntregable() == EstadoEntregable.CANCELADO) {
            throw new PaqueteEntregableCanceladoException(entrada.entregableProyectoGrado(), entrada.version());
        }
    }
}
