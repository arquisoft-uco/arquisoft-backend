package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoCorreccionDisponibleException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemCorreccionDisponibleRule;

public class RevisionItemCorreccionDisponibleRuleImpl implements RevisionItemCorreccionDisponibleRule {

    @Override
    public void validar(EstadoRevisionItem estado) {
        if (!EstadoRevision.CORRECCION_DISPONIBLE.getId().equals(estado.estadoRevisionId())) {
            throw new RevisionItemNoCorreccionDisponibleException(estado.revisionItem());
        }
    }
}
