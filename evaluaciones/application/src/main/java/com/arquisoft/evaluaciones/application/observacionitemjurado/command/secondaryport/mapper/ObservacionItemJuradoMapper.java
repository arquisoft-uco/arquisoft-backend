package com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;

public final class ObservacionItemJuradoMapper {

    private ObservacionItemJuradoMapper() {}

    public static ObservacionItemJuradoDomain toDomain(ObservacionItemJuradoEntity entity) {
        return ObservacionItemJuradoDomain.reconstruir(
                entity.id(), entity.evaluacionCuantitativaJurado(), entity.descripcion());
    }

    public static ObservacionItemJuradoEntity toEntity(ObservacionItemJuradoDomain domain) {
        return new ObservacionItemJuradoEntity(
                domain.getId(), domain.getEvaluacionCuantitativaJurado(), domain.getDescripcion());
    }
}
