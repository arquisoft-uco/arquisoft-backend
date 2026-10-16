package com.arquisoft.proyectos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.asesorproyectogrado.exception.TransicionEstadoAsesorInvalidaException;
import com.arquisoft.proyectos.domain.asesorproyectogrado.model.TransicionEstadoAsesor;
import com.arquisoft.proyectos.domain.asesorproyectogrado.rules.TransicionEstadoAsesorPermitidaRule;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;

public class TransicionEstadoAsesorPermitidaRuleImpl implements TransicionEstadoAsesorPermitidaRule {

    @Override
    public void validar(TransicionEstadoAsesor transicion) {
        if (transicion.actual() != EstadoAsesor.ASIGNADO || transicion.nuevo() != EstadoAsesor.FINALIZADO) {
            throw new TransicionEstadoAsesorInvalidaException(transicion.actual(), transicion.nuevo());
        }
    }
}
