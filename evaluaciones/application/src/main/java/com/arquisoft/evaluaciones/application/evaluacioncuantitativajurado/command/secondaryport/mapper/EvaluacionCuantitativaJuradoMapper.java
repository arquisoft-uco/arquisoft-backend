package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;

public final class EvaluacionCuantitativaJuradoMapper {

    private EvaluacionCuantitativaJuradoMapper() {}

    public static EvaluacionCuantitativaJuradoDomain toDomain(EvaluacionCuantitativaJuradoEntity entity) {
        return EvaluacionCuantitativaJuradoDomain.reconstruir(
                entity.id(), entity.evaluacionJurado(), entity.item(), entity.puntaje());
    }
}
