package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteYaVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculosEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudiantesNoVinculadosArtefactoRule;

import java.util.HashSet;

public class EstudiantesNoVinculadosArtefactoRuleImpl implements EstudiantesNoVinculadosArtefactoRule {

    @Override
    public void validar(VinculosEstudiantesArtefacto vinculos) {
        var yaVinculados = new HashSet<>(vinculos.yaVinculados());
        var vinculado = vinculos.estudiantes().stream()
                .filter(estudiante -> yaVinculados.contains(estudiante.getId()))
                .findFirst();
        if (vinculado.isPresent()) {
            throw new EstudianteYaVinculadoArtefactoException(
                    vinculado.get().getId(), vinculado.get().getNombre());
        }
    }
}
