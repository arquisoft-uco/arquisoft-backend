package com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.impl;

import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.exception.EvaluacionesCualitativasJuradoDuplicadasException;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.DisponibilidadEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.rules.EvaluacionesCualitativasJuradoUnicasRule;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EvaluacionesCualitativasJuradoUnicasRuleImpl implements EvaluacionesCualitativasJuradoUnicasRule {

    @Override
    public void validar(DisponibilidadEvaluacionesCualitativasJurado disponibilidad) {
        Set<UUID> duplicados = new HashSet<>(disponibilidad.itemsSolicitados());
        duplicados.retainAll(disponibilidad.itemsRegistrados());

        if (!duplicados.isEmpty()) {
            throw new EvaluacionesCualitativasJuradoDuplicadasException(duplicados);
        }
    }
}
