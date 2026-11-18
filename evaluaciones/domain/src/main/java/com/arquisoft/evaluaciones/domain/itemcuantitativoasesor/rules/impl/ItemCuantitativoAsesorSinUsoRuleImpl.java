package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorEnUsoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.UsoItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.ItemCuantitativoAsesorSinUsoRule;

public class ItemCuantitativoAsesorSinUsoRuleImpl implements ItemCuantitativoAsesorSinUsoRule {

    @Override
    public void validar(UsoItemCuantitativoAsesor uso) {
        if (uso.enUso()) {
            throw new ItemCuantitativoAsesorEnUsoException(uso.itemCuantitativoAsesor());
        }
    }
}
