package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.FichaPerfilDelItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;

public class ItemFichaPerfilExisteRuleImpl implements ItemFichaPerfilExisteRule {

    @Override
    public void validar(FichaPerfilDelItem fichaDelItem) {
        if (fichaDelItem.fichaPerfil().isEmpty()) {
            throw new ItemFichaPerfilNoEncontradoException(fichaDelItem.item());
        }
    }
}
