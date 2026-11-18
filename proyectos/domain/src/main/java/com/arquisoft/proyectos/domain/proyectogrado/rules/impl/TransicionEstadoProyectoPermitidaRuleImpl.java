package com.arquisoft.proyectos.domain.proyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estadoproyecto.EstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.exception.TransicionEstadoProyectoNoPermitidaException;
import com.arquisoft.proyectos.domain.proyectogrado.model.TransicionEstadoProyecto;
import com.arquisoft.proyectos.domain.proyectogrado.rules.TransicionEstadoProyectoPermitidaRule;

public class TransicionEstadoProyectoPermitidaRuleImpl implements TransicionEstadoProyectoPermitidaRule {

    @Override
    public void validar(TransicionEstadoProyecto transicion) {
        var origenPermitido = transicion.actual() == EstadoProyecto.EN_PROCESO
                || transicion.actual() == EstadoProyecto.ATRASADO;
        if (!origenPermitido || transicion.nuevo() != EstadoProyecto.FINALIZADO) {
            throw new TransicionEstadoProyectoNoPermitidaException(transicion.proyectoGrado(),
                    transicion.tituloProyecto(), transicion.actual(), transicion.nuevo());
        }
    }
}
