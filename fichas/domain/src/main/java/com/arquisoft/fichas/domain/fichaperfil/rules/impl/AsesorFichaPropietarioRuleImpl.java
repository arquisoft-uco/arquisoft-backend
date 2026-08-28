package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;

public class AsesorFichaPropietarioRuleImpl implements AsesorFichaPropietarioRule {

    @Override
    public void validar(PropiedadAsesorFicha propiedad) {
        if (!propiedad.esPropietario()) {
            throw new FichaNoPerteneceAsesorException(propiedad.fichaPerfil(), propiedad.asesorFicha());
        }
    }
}
