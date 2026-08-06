package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemConRevisionesException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.RevisionesItemCriteria;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemSinRevisionesRule;

public class ItemSinRevisionesRuleImpl implements ItemSinRevisionesRule {

    @Override
    public void validar(RevisionesItemCriteria criteria) {
        if (criteria.totalRevisiones() > 0) {
            throw new ItemConRevisionesException(criteria.item());
        }
    }
}
