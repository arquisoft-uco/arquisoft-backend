package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.PertenenciaProyectoGradoEstudiantes;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesVinculadosProyectoGradoRule;

import java.util.HashSet;

public class EstudiantesVinculadosProyectoGradoRuleImpl implements EstudiantesVinculadosProyectoGradoRule {

    @Override
    public void validar(PertenenciaProyectoGradoEstudiantes pertenencia) {
        var vigentes = new HashSet<>(pertenencia.vigentes());
        var faltante = pertenencia.estudiantes().stream()
                .filter(estudiante -> !vigentes.contains(estudiante.getId()))
                .findFirst();
        if (faltante.isPresent()) {
            throw new EstudianteNoVinculadoProyectoGradoException(
                    faltante.get().getId(), faltante.get().getNombre());
        }
    }
}
