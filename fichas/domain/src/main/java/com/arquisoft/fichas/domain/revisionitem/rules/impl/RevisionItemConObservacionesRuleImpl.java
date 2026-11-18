package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemSinObservacionesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemConObservacionesRule;

public class RevisionItemConObservacionesRuleImpl implements RevisionItemConObservacionesRule {

    @Override
    public void validar(ObservacionesRevisionItem observaciones) {
        if (observaciones.total() == 0) {
            throw new RevisionItemSinObservacionesException(observaciones.revisionItem());
        }
    }
}
