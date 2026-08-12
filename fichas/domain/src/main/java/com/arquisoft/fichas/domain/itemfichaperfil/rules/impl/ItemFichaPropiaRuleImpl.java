package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.estudiantefichaperfil.model.PropiedadFicha;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;

public class ItemFichaPropiaRuleImpl implements ItemFichaPropiaRule {

    @Override
    public void validar(PropiedadFicha propiedad) {
        if (!propiedad.esPropietario()) {
            throw new ItemFichaNoPropiaException(propiedad.fichaPerfil());
        }
    }
}
