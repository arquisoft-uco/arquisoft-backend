package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.CategoriaItemCuantitativoAsesorNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.ExistenciaCategoriaItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.CategoriaItemCuantitativoAsesorExistenteRule;

public class CategoriaItemCuantitativoAsesorExistenteRuleImpl
        implements CategoriaItemCuantitativoAsesorExistenteRule {

    @Override
    public void validar(ExistenciaCategoriaItemCuantitativoAsesor existencia) {
        if (!existencia.existe()) {
            throw new CategoriaItemCuantitativoAsesorNoEncontradaException(existencia.categoria());
        }
    }
}
