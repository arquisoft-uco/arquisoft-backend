package com.arquisoft.artefactos.domain.estudiante.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.artefactos.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudianteExisteRule;

public class EstudianteExisteRuleImpl implements EstudianteExisteRule {

    @Override
    public void validar(ExistenciaEstudiante entrada) {
        if (!entrada.existe()) {
            throw new EstudianteNoEncontradoException(entrada.estudiante());
        }
    }
}
