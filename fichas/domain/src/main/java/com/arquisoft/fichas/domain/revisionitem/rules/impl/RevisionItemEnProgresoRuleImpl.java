package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemNoEnProgresoException;
import com.arquisoft.fichas.domain.revisionitem.model.EstadoRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemEnProgresoRule;

public class RevisionItemEnProgresoRuleImpl implements RevisionItemEnProgresoRule {

    @Override
    public void validar(EstadoRevisionItem estado) {
        if (!EstadoRevision.EN_PROGRESO.getId().equals(estado.estadoRevisionId())) {
            throw new RevisionItemNoEnProgresoException(estado.revisionItem());
        }
    }
}
