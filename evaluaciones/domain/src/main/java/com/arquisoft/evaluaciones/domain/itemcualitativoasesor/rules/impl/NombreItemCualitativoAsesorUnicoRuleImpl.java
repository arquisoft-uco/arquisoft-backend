package com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.exception.NombreItemCualitativoAsesorDuplicadoException;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.model.DisponibilidadNombreItemCualitativoAsesor;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.rules.NombreItemCualitativoAsesorUnicoRule;

public class NombreItemCualitativoAsesorUnicoRuleImpl implements NombreItemCualitativoAsesorUnicoRule {

    @Override
    public void validar(DisponibilidadNombreItemCualitativoAsesor disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new NombreItemCualitativoAsesorDuplicadoException(disponibilidad.nombre());
        }
    }
}
