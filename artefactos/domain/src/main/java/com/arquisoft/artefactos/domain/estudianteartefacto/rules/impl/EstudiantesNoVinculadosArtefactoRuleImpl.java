package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteYaVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculosEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesNoVinculadosArtefactoRule;
import com.arquisoft.shared.util.UtilTexto;

public class EstudiantesNoVinculadosArtefactoRuleImpl implements EstudiantesNoVinculadosArtefactoRule {

    @Override
    public void validar(VinculosEstudiantesArtefacto vinculos) {
        var primero = vinculos.yaVinculados().stream().findFirst();
        if (primero.isPresent()) {
            var estudiante = primero.get().estudiante();
            var nombre = vinculos.estudiantes().stream()
                    .filter(encontrado -> encontrado.getId().equals(estudiante))
                    .map(EstudianteDomain::getNombre)
                    .findFirst()
                    .orElse(UtilTexto.VACIO);
            throw new EstudianteYaVinculadoArtefactoException(estudiante, nombre);
        }
    }
}
