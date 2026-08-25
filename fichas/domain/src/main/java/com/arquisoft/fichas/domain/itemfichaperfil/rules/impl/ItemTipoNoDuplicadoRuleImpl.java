package com.arquisoft.fichas.domain.itemfichaperfil.rules.impl;

import com.arquisoft.fichas.domain.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.model.DisponibilidadTipoItem;
import com.arquisoft.fichas.domain.itemfichaperfil.rules.ItemTipoNoDuplicadoRule;

public class ItemTipoNoDuplicadoRuleImpl implements ItemTipoNoDuplicadoRule {

    @Override
    public void validar(DisponibilidadTipoItem disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new ItemTipoDuplicadoException(disponibilidad.tipoItem().getId());
        }
    }
}
