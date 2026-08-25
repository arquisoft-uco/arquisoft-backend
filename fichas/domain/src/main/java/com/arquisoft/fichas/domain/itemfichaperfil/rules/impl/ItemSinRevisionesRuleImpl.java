package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;

public class ItemSinRevisionesRuleImpl implements ItemSinRevisionesRule {

    @Override
    public void validar(RevisionesItem revisiones) {
        if (revisiones.totalRevisiones() > 0) {
            throw new ItemConRevisionesException(revisiones.item());
        }
    }
}
