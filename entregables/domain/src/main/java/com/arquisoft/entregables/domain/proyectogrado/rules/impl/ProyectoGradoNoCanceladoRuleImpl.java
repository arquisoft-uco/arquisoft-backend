package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoCanceladoException;
import com.arquisoft.entregables.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoNoCanceladoRule;

public class ProyectoGradoNoCanceladoRuleImpl implements ProyectoGradoNoCanceladoRule {

    @Override
    public void validar(EstadoActualProyectoGrado estado) {
        if (estado.estadoActual() == EstadoProyecto.CANCELADO) {
            throw new ProyectoGradoCanceladoException(estado.proyectoGrado(), estado.nombre());
        }
    }
}
