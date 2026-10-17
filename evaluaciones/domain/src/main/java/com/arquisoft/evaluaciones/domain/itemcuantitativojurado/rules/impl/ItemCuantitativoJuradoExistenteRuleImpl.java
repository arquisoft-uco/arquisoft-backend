package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.ItemCuantitativoJuradoNoEncontradoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.ExistenciaItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.ItemCuantitativoJuradoExistenteRule;

public class ItemCuantitativoJuradoExistenteRuleImpl implements ItemCuantitativoJuradoExistenteRule {

    @Override
    public void validar(ExistenciaItemCuantitativoJurado existencia) {
        if (!existencia.existe()) {
            throw new ItemCuantitativoJuradoNoEncontradoException(existencia.itemCuantitativoJurado());
        }
    }
}
