package com.arquisoft.fichas.domain.revisionitem.rules.impl;

import com.arquisoft.fichas.domain.revisionitem.exception.RevisionItemYaExisteException;
import com.arquisoft.fichas.domain.revisionitem.model.DisponibilidadRevisionItem;
import com.arquisoft.fichas.domain.revisionitem.rules.RevisionItemNoDuplicadaRule;

public class RevisionItemNoDuplicadaRuleImpl implements RevisionItemNoDuplicadaRule {

    @Override
    public void validar(DisponibilidadRevisionItem disponibilidad) {
        if (disponibilidad.cantidadRevisiones() > 0) {
            throw new RevisionItemYaExisteException(disponibilidad.item());
        }
    }
}
