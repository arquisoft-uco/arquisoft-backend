package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.ItemCualitativoAsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.ExistenciaItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.ItemCualitativoAsesorExistenteRule;

public class ItemCualitativoAsesorExistenteRuleImpl implements ItemCualitativoAsesorExistenteRule {

    @Override
    public void validar(ExistenciaItemCualitativoAsesor existencia) {
        if (!existencia.existe()) {
            throw new ItemCualitativoAsesorNoEncontradoException(existencia.itemCualitativoAsesor());
        }
    }
}
