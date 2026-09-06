package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.exception.FichaNoPerteneceAsesorException;
import com.arquisoft.fichas.domain.fichaperfil.model.PropiedadAsesorFicha;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaPropietarioRule;
import com.arquisoft.shared.util.UtilObjeto;

public class AsesorFichaPropietarioRuleImpl implements AsesorFichaPropietarioRule {

    @Override
    public void validar(PropiedadAsesorFicha propiedad) {
        if (UtilObjeto.esNulo(propiedad.asesorEsperado())
                || !propiedad.asesorEsperado().equals(propiedad.asesorSolicitante())) {
            throw new FichaNoPerteneceAsesorException(propiedad.fichaPerfil(), propiedad.asesorSolicitante());
        }
    }
}
