package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoCanceladoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoNoCanceladoRule;

public class ProyectoGradoNoCanceladoRuleImpl implements ProyectoGradoNoCanceladoRule {

    @Override
    public void validar(EstadoActualProyectoGrado estado) {
        if (estado.estadoActual() == EstadoProyecto.CANCELADO) {
            throw new ProyectoGradoCanceladoException(estado.proyectoGrado(), estado.tituloProyecto());
        }
    }
}
