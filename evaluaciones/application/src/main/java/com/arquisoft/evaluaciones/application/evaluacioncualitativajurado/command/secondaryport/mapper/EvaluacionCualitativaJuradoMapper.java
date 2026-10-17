package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;

public final class EvaluacionCualitativaJuradoMapper {

    private EvaluacionCualitativaJuradoMapper() {}

    public static EvaluacionCualitativaJuradoEntity toEntity(EvaluacionCualitativaJuradoDomain domain) {
        return new EvaluacionCualitativaJuradoEntity(
                domain.getId(), domain.getEvaluacionJurado(), domain.getItem(), domain.getCriterio());
    }

    public static EvaluacionCualitativaJuradoDomain toDomain(EvaluacionCualitativaJuradoEntity entity) {
        return EvaluacionCualitativaJuradoDomain.reconstruir(
                entity.id(), entity.evaluacionJurado(), entity.item(), entity.criterio());
    }
}
