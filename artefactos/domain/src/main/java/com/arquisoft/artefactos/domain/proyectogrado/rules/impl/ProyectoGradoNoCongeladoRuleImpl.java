package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoCongeladoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoNoCongeladoRule;

public class ProyectoGradoNoCongeladoRuleImpl implements ProyectoGradoNoCongeladoRule {

    @Override
    public void validar(EstadoProyectoGrado estado) {
        if (estado.congelado()) {
            throw new ProyectoGradoCongeladoException(estado);
        }
    }
}
