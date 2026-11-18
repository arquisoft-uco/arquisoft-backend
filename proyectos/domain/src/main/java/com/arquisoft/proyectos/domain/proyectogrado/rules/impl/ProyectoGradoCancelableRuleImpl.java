package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.ProyectoGradoNoCancelableException;
import com.arquisoft.proyectos.domain.proyectogrado.model.EstadoActualProyectoGrado;
import com.arquisoft.proyectos.domain.proyectogrado.rules.ProyectoGradoCancelableRule;

public class ProyectoGradoCancelableRuleImpl implements ProyectoGradoCancelableRule {

    @Override
    public void validar(EstadoActualProyectoGrado estado) {
        if (estado.estadoActual() != EstadoProyecto.EN_PROCESO && estado.estadoActual() != EstadoProyecto.ATRASADO) {
            throw new ProyectoGradoNoCancelableException(
                    estado.proyectoGrado(), estado.tituloProyecto(), estado.estadoActual());
        }
    }
}
