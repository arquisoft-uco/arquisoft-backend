package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.ItemCuantitativoAsesorNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.ItemCuantitativoAsesorExistenteRule;

public class ItemCuantitativoAsesorExistenteRuleImpl implements ItemCuantitativoAsesorExistenteRule {

    @Override
    public void validar(ExistenciaItemCuantitativoAsesor existencia) {
        if (!existencia.existe()) {
            throw new ItemCuantitativoAsesorNoEncontradoException(existencia.itemCuantitativoAsesor());
        }
    }
}
