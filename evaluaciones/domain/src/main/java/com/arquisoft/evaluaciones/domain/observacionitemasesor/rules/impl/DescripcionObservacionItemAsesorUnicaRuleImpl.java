package com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemasesor.exception.DescripcionObservacionItemAsesorDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.model.DisponibilidadDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.rules.DescripcionObservacionItemAsesorUnicaRule;

public class DescripcionObservacionItemAsesorUnicaRuleImpl implements DescripcionObservacionItemAsesorUnicaRule {

    @Override
    public void validar(DisponibilidadDescripcionObservacionItemAsesor disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new DescripcionObservacionItemAsesorDuplicadaException(disponibilidad.evaluacionCuantitativaAsesor());
        }
    }
}
