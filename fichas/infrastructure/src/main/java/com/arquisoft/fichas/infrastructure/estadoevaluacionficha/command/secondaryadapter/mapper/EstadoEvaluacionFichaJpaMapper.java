package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.mapper.EstadoEvaluacionJpaMapper;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.entity.EstadoEvaluacionFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.mapper.EvaluacionFichaPerfilJpaMapper;

public final class EstadoEvaluacionFichaJpaMapper {

    private EstadoEvaluacionFichaJpaMapper() {}

    public static EstadoEvaluacionFichaEntity toEntity(EstadoEvaluacionFichaJpaEntity jpaEntity) {
        return new EstadoEvaluacionFichaEntity(
                jpaEntity.getId(),
                jpaEntity.getEvaluacionFichaPerfil().getId(),
                jpaEntity.getEstadoEvaluacion().getId(),
                jpaEntity.getFechaActualizacion());
    }

    public static EstadoEvaluacionFichaJpaEntity toJpaEntity(EstadoEvaluacionFichaEntity entity) {
        return EstadoEvaluacionFichaJpaEntity.builder()
                .id(entity.id())
                .evaluacionFichaPerfil(EvaluacionFichaPerfilJpaMapper.toReferencia(entity.evaluacionFichaPerfil()))
                .estadoEvaluacion(EstadoEvaluacionJpaMapper.toReferencia(entity.estadoEvaluacion()))
                .fechaActualizacion(entity.fechaActualizacion())
                .build();
    }
}
