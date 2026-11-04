package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.EstudianteNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefacto;
import com.arquisoft.artefactos.domain.artefacto.rules.EstudiantePropietarioArtefactoRule;

public class EstudiantePropietarioArtefactoRuleImpl implements EstudiantePropietarioArtefactoRule {

    @Override
    public void validar(PropiedadArtefacto propiedad) {
        if (!propiedad.vinculado()) {
            throw new EstudianteNoPropietarioArtefactoException(propiedad.estudiante(), propiedad.artefacto());
        }
    }
}
