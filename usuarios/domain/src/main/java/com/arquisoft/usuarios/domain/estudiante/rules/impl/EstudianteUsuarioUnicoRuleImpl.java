package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteUsuarioUnicoRule;

public class EstudianteUsuarioUnicoRuleImpl implements EstudianteUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadEstudianteUsuario disponibilidad) {
        if (disponibilidad.yaEsEstudiante()) {
            throw new EstudianteUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
