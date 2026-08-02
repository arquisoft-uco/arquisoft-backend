package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ItemTipoCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;

public class ItemTipoNoDuplicadoRuleImpl implements ItemTipoNoDuplicadoRule {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    public ItemTipoNoDuplicadoRuleImpl(ItemFichaPerfilOutputPort itemFichaPerfilOutputPort) {
        this.itemFichaPerfilOutputPort = itemFichaPerfilOutputPort;
    }

    @Override
    public void validar(ItemTipoCriteria criteria) {
        if (itemFichaPerfilOutputPort.existePorFichaYTipoItem(criteria.fichaPerfil(), criteria.tipoItem())) {
            throw new ItemTipoDuplicadoException(criteria.tipoItem());
        }
    }
}
