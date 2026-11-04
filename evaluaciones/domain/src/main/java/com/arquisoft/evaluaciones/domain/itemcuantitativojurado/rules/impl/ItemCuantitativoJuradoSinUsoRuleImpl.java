package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.UsoItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.ItemCuantitativoJuradoSinUsoRule;

public class ItemCuantitativoJuradoSinUsoRuleImpl implements ItemCuantitativoJuradoSinUsoRule {

    @Override
    public void validar(UsoItemCuantitativoJurado uso) {
        if (uso.enUso()) {
            throw new ItemCuantitativoJuradoEnUsoException(uso.itemCuantitativoJurado());
        }
    }
}
