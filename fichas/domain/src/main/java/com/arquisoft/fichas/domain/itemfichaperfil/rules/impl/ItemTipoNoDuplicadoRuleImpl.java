package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.ItemFichaPerfilDomain;
import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;

public class ItemTipoNoDuplicadoRuleImpl implements ItemTipoNoDuplicadoRule {

    private final ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    public ItemTipoNoDuplicadoRuleImpl(ItemFichaPerfilOutputPort itemFichaPerfilOutputPort) {
        this.itemFichaPerfilOutputPort = itemFichaPerfilOutputPort;
    }

    @Override
    public void validar(ItemFichaPerfilDomain item) {
        if (itemFichaPerfilOutputPort.existePorFichaYTipoItem(item.getFichaPerfilId(), item.getTipoItem().getId())) {
            throw new ItemTipoDuplicadoException(item.getTipoItem().getId());
        }
    }
}
