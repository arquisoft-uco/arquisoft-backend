package com.arquisoft.artefactos.domain.estudiante.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudiante.exception.EstudianteNoVigenteException;
import com.arquisoft.artefactos.domain.estudiante.model.VigenciaEstudiantes;
import com.arquisoft.artefactos.domain.estudiante.rules.EstudiantesVigentesRule;

public class EstudiantesVigentesRuleImpl implements EstudiantesVigentesRule {

    @Override
    public void validar(VigenciaEstudiantes vigencia) {
        var eliminado = vigencia.estudiantes().stream()
                .filter(EstudianteDomain::estaEliminado)
                .findFirst();
        if (eliminado.isPresent()) {
            throw new EstudianteNoVigenteException(eliminado.get().getId(), eliminado.get().getNombre());
        }
    }
}
