package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemConObservacionesPendientesException;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemSinObservacionesPendientesRule;

public class RevisionItemSinObservacionesPendientesRuleImpl implements RevisionItemSinObservacionesPendientesRule {

    @Override
    public void validar(ObservacionesRevisionItem observaciones) {
        if (observaciones.pendientes() > 0) {
            throw new RevisionItemConObservacionesPendientesException(
                    observaciones.revisionItem(), observaciones.pendientes());
        }
    }
}
