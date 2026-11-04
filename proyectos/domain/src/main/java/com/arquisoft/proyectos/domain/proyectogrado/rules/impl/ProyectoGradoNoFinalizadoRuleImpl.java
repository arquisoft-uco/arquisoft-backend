package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;

public class ProyectoGradoNoFinalizadoRuleImpl implements ProyectoGradoNoFinalizadoRule {

    @Override
    public void validar(EstadoActualProyectoGrado estado) {
        if (estado.estadoActual() == EstadoProyecto.FINALIZADO) {
            throw new ProyectoGradoFinalizadoException(estado.proyectoGrado(), estado.tituloProyecto());
        }
    }
}
