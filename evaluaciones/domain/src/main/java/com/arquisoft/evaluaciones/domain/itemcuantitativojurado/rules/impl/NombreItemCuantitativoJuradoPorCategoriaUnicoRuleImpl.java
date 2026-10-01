package com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.exception.NombreItemCuantitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.model.DisponibilidadNombreItemCuantitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.rules.NombreItemCuantitativoJuradoPorCategoriaUnicoRule;

public class NombreItemCuantitativoJuradoPorCategoriaUnicoRuleImpl
        implements NombreItemCuantitativoJuradoPorCategoriaUnicoRule {

    @Override
    public void validar(DisponibilidadNombreItemCuantitativoJurado disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new NombreItemCuantitativoJuradoDuplicadoException(
                    disponibilidad.nombre(), disponibilidad.categoria());
        }
    }
}
