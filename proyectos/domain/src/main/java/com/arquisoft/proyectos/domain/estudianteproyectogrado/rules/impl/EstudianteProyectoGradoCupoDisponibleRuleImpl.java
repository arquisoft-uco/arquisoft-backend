package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.CupoEstudiantesExcedidoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.CupoEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudianteProyectoGradoCupoDisponibleRule;
import com.arquisoft.shared.message.constant.ProyectosLimits;

public class EstudianteProyectoGradoCupoDisponibleRuleImpl implements EstudianteProyectoGradoCupoDisponibleRule {

    @Override
    public void validar(CupoEstudiantesProyectoGrado cupo) {
        if (cupo.yaVinculados() + cupo.nuevos() > ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES) {
            throw new CupoEstudiantesExcedidoException(ProyectosLimits.EstudianteProyectoGrado.MAX_ESTUDIANTES);
        }
    }
}
