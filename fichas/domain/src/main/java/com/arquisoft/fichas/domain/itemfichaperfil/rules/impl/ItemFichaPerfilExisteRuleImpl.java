package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemFichaPerfilNoEncontradoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.ExistenciaItemFichaPerfil;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemFichaPerfilExisteRule;

public class ItemFichaPerfilExisteRuleImpl implements ItemFichaPerfilExisteRule {

    @Override
    public void validar(ExistenciaItemFichaPerfil existencia) {
        if (!existencia.existe()) {
            throw new ItemFichaPerfilNoEncontradoException(existencia.item());
        }
    }
}
