package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoFinalizadoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoFinalizadoRule;

public class ProyectoGradoFinalizadoRuleImpl implements ProyectoGradoFinalizadoRule {

    @Override
    public void validar(EstadoProyectoGrado estado) {
        if (!estado.finalizado()) {
            throw new ProyectoGradoNoFinalizadoException(estado);
        }
    }
}
