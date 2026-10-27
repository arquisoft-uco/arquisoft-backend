package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoProyectoGradoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.PertenenciaProyectoGradoEstudiantes;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesVinculadosProyectoGradoRule;
import com.arquisoft.shared.util.UtilTexto;

import java.util.HashSet;

public class EstudiantesVinculadosProyectoGradoRuleImpl implements EstudiantesVinculadosProyectoGradoRule {

    @Override
    public void validar(PertenenciaProyectoGradoEstudiantes pertenencia) {
        var vigentes = new HashSet<>(pertenencia.vigentes());
        var faltante = pertenencia.solicitados().stream()
                .filter(par -> !vigentes.contains(par))
                .findFirst();
        if (faltante.isPresent()) {
            var estudiante = faltante.get().estudiante();
            var nombre = pertenencia.estudiantes().stream()
                    .filter(encontrado -> encontrado.getId().equals(estudiante))
                    .map(EstudianteDomain::getNombre)
                    .findFirst()
                    .orElse(UtilTexto.VACIO);
            throw new EstudianteNoVinculadoProyectoGradoException(estudiante, nombre);
        }
    }
}
