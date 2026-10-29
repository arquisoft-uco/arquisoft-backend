package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactoRule;

public class AsesorPropietarioArtefactoRuleImpl implements AsesorPropietarioArtefactoRule {

    @Override
    public void validar(PropiedadArtefactoAsesor propiedad) {
        if (!propiedad.artefacto().getAsesor().equals(propiedad.asesor())) {
            throw new AsesorNoPropietarioArtefactoException(propiedad.asesor(), propiedad.artefacto().getId());
        }
    }
}
