package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.ExistenciaItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoExistenteRule;

public class ItemCualitativoJuradoExistenteRuleImpl implements ItemCualitativoJuradoExistenteRule {

    @Override
    public void validar(ExistenciaItemCualitativoJurado existencia) {
        if (!existencia.existe()) {
            throw new ItemCualitativoJuradoNoEncontradoException(existencia.itemCualitativoJurado());
        }
    }
}
