package com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.impl;

import com.arquisoft.proyectos.domain.estudianteproyectogrado.exception.EstudiantesNoVigentesException;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.model.VigenciaEstudiantesProyectoGrado;
import com.arquisoft.proyectos.domain.estudianteproyectogrado.rules.EstudiantesVigentesProyectoGradoRule;

public class EstudiantesVigentesProyectoGradoRuleImpl implements EstudiantesVigentesProyectoGradoRule {

    @Override
    public void validar(VigenciaEstudiantesProyectoGrado vigencia) {
        var faltantes = vigencia.solicitados().stream()
                .filter(estudiante -> !vigencia.vigentes().contains(estudiante))
                .sorted()
                .toList();
        if (!faltantes.isEmpty()) {
            throw new EstudiantesNoVigentesException(faltantes);
        }
    }
}
