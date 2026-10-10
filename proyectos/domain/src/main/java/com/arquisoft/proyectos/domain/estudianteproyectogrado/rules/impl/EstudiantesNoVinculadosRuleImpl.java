package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudianteYaVinculadoException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VinculosEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesNoVinculadosRule;

public class EstudiantesNoVinculadosRuleImpl implements EstudiantesNoVinculadosRule {

    @Override
    public void validar(VinculosEstudiantesProyectoGrado vinculos) {
        if (!vinculos.yaVinculados().isEmpty()) {
            throw new EstudianteYaVinculadoException(vinculos.yaVinculados().getFirst());
        }
    }
}
