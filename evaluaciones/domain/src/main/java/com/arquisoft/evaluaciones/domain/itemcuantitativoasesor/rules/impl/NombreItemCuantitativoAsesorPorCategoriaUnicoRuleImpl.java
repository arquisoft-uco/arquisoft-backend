package com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.exception.NombreItemCuantitativoAsesorDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.model.DisponibilidadNombreItemCuantitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.rules.NombreItemCuantitativoAsesorPorCategoriaUnicoRule;

public class NombreItemCuantitativoAsesorPorCategoriaUnicoRuleImpl
        implements NombreItemCuantitativoAsesorPorCategoriaUnicoRule {

    @Override
    public void validar(DisponibilidadNombreItemCuantitativoAsesor disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new NombreItemCuantitativoAsesorDuplicadoException(
                    disponibilidad.nombre(), disponibilidad.categoria());
        }
    }
}
