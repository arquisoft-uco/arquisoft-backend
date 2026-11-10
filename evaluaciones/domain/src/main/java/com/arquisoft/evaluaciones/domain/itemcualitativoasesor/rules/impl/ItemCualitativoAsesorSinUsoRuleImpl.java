package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.UsoItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.ItemCualitativoAsesorSinUsoRule;

public class ItemCualitativoAsesorSinUsoRuleImpl implements ItemCualitativoAsesorSinUsoRule {

    @Override
    public void validar(UsoItemCualitativoAsesor uso) {
        if (uso.enUso()) {
            throw new ItemCualitativoAsesorEnUsoException(uso.itemCualitativoAsesor());
        }
    }
}
