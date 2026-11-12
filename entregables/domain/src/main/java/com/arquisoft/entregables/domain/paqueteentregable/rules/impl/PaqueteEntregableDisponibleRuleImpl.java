package com.arquisoft.entregables.domain.paqueteentregable.rules.impl;

import com.arquisoft.entregables.domain.paqueteentregable.exception.PaqueteEntregableNoDisponibleException;
import com.arquisoft.entregables.domain.paqueteentregable.model.DisponibilidadPaqueteEntregable;
import com.arquisoft.entregables.domain.paqueteentregable.rules.PaqueteEntregableDisponibleRule;

public class PaqueteEntregableDisponibleRuleImpl implements PaqueteEntregableDisponibleRule {

    @Override
    public void validar(DisponibilidadPaqueteEntregable entrada) {
        if (!entrada.disponible()) {
            throw new PaqueteEntregableNoDisponibleException(entrada.entregableProyectoGrado());
        }
    }
}
