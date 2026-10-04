package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.CategoriaItemCuantitativoJuradoNoEncontradaException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaCategoriaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.CategoriaItemCuantitativoJuradoExistenteRule;

public class CategoriaItemCuantitativoJuradoExistenteRuleImpl
        implements CategoriaItemCuantitativoJuradoExistenteRule {

    @Override
    public void validar(ExistenciaCategoriaItemCuantitativoJurado existencia) {
        if (!existencia.existe()) {
            throw new CategoriaItemCuantitativoJuradoNoEncontradaException(
                    existencia.categoria());
        }
    }
}
