package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.model.PropietarioFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPropiaRule;

public class ItemFichaPropiaRuleImpl implements ItemFichaPropiaRule {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;

    public ItemFichaPropiaRuleImpl(FichaPerfilOutputPort fichaPerfilOutputPort) {
        this.fichaPerfilOutputPort = fichaPerfilOutputPort;
    }

    @Override
    public void validar(PropietarioFichaCriteria criteria) {
        if (!fichaPerfilOutputPort.esEstudiantePropietario(criteria)) {
            throw new ItemFichaNoPropiaException(criteria.fichaPerfil());
        }
    }
}
