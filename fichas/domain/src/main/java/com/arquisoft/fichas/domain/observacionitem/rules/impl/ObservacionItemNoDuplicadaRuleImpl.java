package com.arquisoft.fichas.domain.observacionitem.rules.impl;

import com.arquisoft.fichas.domain.observacionitem.exception.ObservacionItemDuplicadaException;
import com.arquisoft.fichas.domain.observacionitem.model.DisponibilidadObservacionItem;
import com.arquisoft.fichas.domain.observacionitem.rules.ObservacionItemNoDuplicadaRule;

public class ObservacionItemNoDuplicadaRuleImpl implements ObservacionItemNoDuplicadaRule {

    @Override
    public void validar(DisponibilidadObservacionItem disponibilidad) {
        if (disponibilidad.cantidadCoincidencias() > 0) {
            throw new ObservacionItemDuplicadaException(disponibilidad.revisionItem(), disponibilidad.observacion());
        }
    }
}
