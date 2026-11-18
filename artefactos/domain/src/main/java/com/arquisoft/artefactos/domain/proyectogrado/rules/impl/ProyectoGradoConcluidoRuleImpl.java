package com.arquisoft.artefactos.domain.proyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.proyectogrado.exception.ProyectoGradoNoConcluidoException;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.rules.ProyectoGradoConcluidoRule;

public class ProyectoGradoConcluidoRuleImpl implements ProyectoGradoConcluidoRule {

    @Override
    public void validar(EstadoProyectoGrado estado) {
        if (!estado.concluido()) {
            throw new ProyectoGradoNoConcluidoException(estado);
        }
    }
}
