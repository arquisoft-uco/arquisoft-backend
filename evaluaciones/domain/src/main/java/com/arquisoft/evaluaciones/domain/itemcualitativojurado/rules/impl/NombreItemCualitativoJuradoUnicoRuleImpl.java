package com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.exception.NombreItemCualitativoJuradoDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.model.DisponibilidadNombreItemCualitativoJurado;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.rules.NombreItemCualitativoJuradoUnicoRule;

public class NombreItemCualitativoJuradoUnicoRuleImpl
        implements NombreItemCualitativoJuradoUnicoRule {

    @Override
    public void validar(DisponibilidadNombreItemCualitativoJurado disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new NombreItemCualitativoJuradoDuplicadoException(disponibilidad.nombre());
        }
    }
}
