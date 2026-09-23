package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.ItemCualitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.UsoItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.ItemCualitativoJuradoSinUsoRule;

public class ItemCualitativoJuradoSinUsoRuleImpl implements ItemCualitativoJuradoSinUsoRule {

    @Override
    public void validar(UsoItemCualitativoJurado uso) {
        if (uso.enUso()) {
            throw new ItemCualitativoJuradoEnUsoException(uso.itemCualitativoJurado());
        }
    }
}
