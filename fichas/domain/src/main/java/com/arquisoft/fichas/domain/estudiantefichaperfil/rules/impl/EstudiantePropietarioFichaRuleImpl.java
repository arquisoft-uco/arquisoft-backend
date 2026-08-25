package com.arquisoft.fichas.domain.estudiantefichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.estudiantefichaperfil.rules.EstudiantePropietarioFichaRule;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPropietarioException;

public class EstudiantePropietarioFichaRuleImpl implements EstudiantePropietarioFichaRule {

    @Override
    public void validar(PropiedadFicha propiedad) {
        if (!propiedad.esPropietario()) {
            throw new FichaNoPropietarioException(propiedad.fichaPerfil(), propiedad.estudiante());
        }
    }
}
