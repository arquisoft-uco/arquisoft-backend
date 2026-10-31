package com.arquisoft.artefactos.domain.estudianteartefacto.rules.impl;

import com.arquisoft.artefactos.domain.estudianteartefacto.exception.EstudianteNoVinculadoArtefactoException;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.VinculoEstudianteArtefacto;
import com.arquisoft.artefactos.domain.estudianteartefacto.rules.EstudianteVinculadoArtefactoRule;

public class EstudianteVinculadoArtefactoRuleImpl implements EstudianteVinculadoArtefactoRule {

    @Override
    public void validar(VinculoEstudianteArtefacto vinculo) {
        if (!vinculo.vinculado()) {
            throw new EstudianteNoVinculadoArtefactoException(
                    vinculo.estudiante(), vinculo.artefacto(), vinculo.nombreEstudiante());
        }
    }
}
