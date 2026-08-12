package com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadoevaluacion.command.secondaryport.entity.EstadoEvaluacionEntity;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;

public final class EstadoEvaluacionFichaMapper {

    private EstadoEvaluacionFichaMapper() {}

    public static EstadoEvaluacionFichaDomain toDomain(EstadoEvaluacionFichaEntity entity) {
        return EstadoEvaluacionFichaDomain.reconstruir(
                entity.getId(),
                entity.getEvaluacionFichaPerfil().getId(),
                convertir(entity.getEstadoEvaluacion().getId()),
                entity.getFechaActualizacion());
    }

    private static EstadoEvaluacion convertir(String estadoEvaluacionId) {
        try {
            return EstadoEvaluacion.valueOf(estadoEvaluacionId);
        } catch (IllegalArgumentException ex) {
            throw new EstadoEvaluacionNoEncontradoException(estadoEvaluacionId);
        }
    }

    public static EstadoEvaluacionFichaEntity toEntity(EstadoEvaluacionFichaDomain aggregate) {
        return EstadoEvaluacionFichaEntity.builder()
                .id(aggregate.getId())
                .evaluacionFichaPerfil(EvaluacionFichaPerfilEntity.builder()
                        .id(aggregate.getEvaluacionFichaPerfilId())
                        .build())
                .estadoEvaluacion(EstadoEvaluacionEntity.builder()
                        .id(aggregate.getEstadoEvaluacion().name())
                        .build())
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }
}
