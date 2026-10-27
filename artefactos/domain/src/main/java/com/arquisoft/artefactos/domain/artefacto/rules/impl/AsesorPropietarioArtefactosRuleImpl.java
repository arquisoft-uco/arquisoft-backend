package com.arquisoft.artefactos.domain.artefacto.rules.impl;

import com.arquisoft.artefactos.domain.artefacto.exception.AsesorNoPropietarioArtefactoException;
import com.arquisoft.artefactos.domain.artefacto.model.PropiedadArtefactosAsesor;
import com.arquisoft.artefactos.domain.artefacto.rules.AsesorPropietarioArtefactosRule;

public class AsesorPropietarioArtefactosRuleImpl implements AsesorPropietarioArtefactosRule {

    @Override
    public void validar(PropiedadArtefactosAsesor propiedad) {
        var ajeno = propiedad.artefactos().stream()
                .filter(artefacto -> !artefacto.getAsesor().equals(propiedad.asesor()))
                .findFirst();
        if (ajeno.isPresent()) {
            throw new AsesorNoPropietarioArtefactoException(propiedad.asesor(), ajeno.get().getId());
        }
    }
}
