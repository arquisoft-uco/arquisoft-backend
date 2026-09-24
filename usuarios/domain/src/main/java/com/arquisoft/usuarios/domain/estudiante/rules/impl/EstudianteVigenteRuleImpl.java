package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.usuarios.domain.estudiante.model.ExistenciaEstudiante;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteVigenteRule;

public class EstudianteVigenteRuleImpl implements EstudianteVigenteRule {

    @Override
    public void validar(ExistenciaEstudiante existencia) {
        var estudiante = existencia.estudiante();
        if (estudiante.esVacio() || estudiante.estaEliminado()) {
            throw new EstudianteNoEncontradoException(existencia.usuario());
        }
    }
}
