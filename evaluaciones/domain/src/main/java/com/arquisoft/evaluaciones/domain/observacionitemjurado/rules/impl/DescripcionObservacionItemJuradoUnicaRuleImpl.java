package com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.impl;

import com.arquisoft.evaluaciones.domain.observacionitemjurado.exception.DescripcionObservacionItemJuradoDuplicadaException;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.model.DisponibilidadDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.rules.DescripcionObservacionItemJuradoUnicaRule;

public class DescripcionObservacionItemJuradoUnicaRuleImpl implements DescripcionObservacionItemJuradoUnicaRule {

    @Override
    public void validar(DisponibilidadDescripcionObservacionItemJurado disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new DescripcionObservacionItemJuradoDuplicadaException(disponibilidad.evaluacionCuantitativaJurado());
        }
    }
}
