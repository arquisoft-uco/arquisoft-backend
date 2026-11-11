package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableProyectoCanceladoException;
import com.arquisoft.entregables.domain.paqueteentregable.model.ProyectoDelPaquete;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableProyectoNoCanceladoRule;

public class PaqueteEntregableProyectoNoCanceladoRuleImpl implements PaqueteEntregableProyectoNoCanceladoRule {

    @Override
    public void validar(ProyectoDelPaquete entrada) {
        if (entrada.cancelado()) {
            throw new PaqueteEntregableProyectoCanceladoException(entrada.entregableProyectoGrado());
        }
    }
}
