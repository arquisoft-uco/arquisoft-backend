package com.arquisoft.entregables.domain.proyectogrado.rules.impl;

import com.arquisoft.entregables.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.entregables.domain.proyectogrado.exception.ProyectoGradoFinalizadoException;
import com.arquisoft.entregables.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.entregables.domain.proyectogrado.rules.ProyectoGradoNoFinalizadoRule;

public class ProyectoGradoNoFinalizadoRuleImpl implements ProyectoGradoNoFinalizadoRule {

    @Override
    public void validar(EstadoActualProyectoGrado estado) {
        if (estado.estadoActual() == EstadoProyecto.FINALIZADO) {
            throw new ProyectoGradoFinalizadoException(estado.proyectoGrado(), estado.nombre());
        }
    }
}
