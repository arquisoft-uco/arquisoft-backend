package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionesCualitativasJuradoUnicasRule;

import java.util.HashSet;

public class EvaluacionesCualitativasJuradoUnicasRuleImpl implements EvaluacionesCualitativasJuradoUnicasRule {

    @Override
    public void validar(DisponibilidadEvaluacionesCualitativasJurado disponibilidad) {
        var duplicados = new HashSet<>(disponibilidad.itemsSolicitados());
        duplicados.retainAll(disponibilidad.itemsRegistrados());

        if (!duplicados.isEmpty()) {
            throw new EvaluacionesCualitativasJuradoDuplicadasException(duplicados);
        }
    }
}
