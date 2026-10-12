package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemCerradaException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoCerradaRule;

public class RevisionItemNoCerradaRuleImpl implements RevisionItemNoCerradaRule {

    @Override
    public void validar(EstadoRevisionItem estado) {
        if (EstadoRevision.CERRADA.getId().equals(estado.estadoRevisionId())) {
            throw new RevisionItemCerradaException(estado.revisionItem());
        }
    }
}
