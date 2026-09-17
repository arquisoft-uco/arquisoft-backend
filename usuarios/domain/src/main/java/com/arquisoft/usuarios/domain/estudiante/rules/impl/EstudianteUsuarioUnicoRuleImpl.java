package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import com.arquisoft.usuarios.domain.estudiante.rules.EstudianteUsuarioUnicoRule;

public class EstudianteUsuarioUnicoRuleImpl implements EstudianteUsuarioUnicoRule {

    @Override
    public void validar(DisponibilidadEstudianteUsuario disponibilidad) {
        var estudiante = disponibilidad.estudiante();
        if (!estudiante.esVacio() && !estudiante.estaEliminado()) {
            throw new EstudianteUsuarioDuplicadoException(disponibilidad.usuario());
        }
    }
}
