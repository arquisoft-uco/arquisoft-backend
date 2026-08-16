package com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;

public final class EstadoEvaluacionFichaMapper {

    private EstadoEvaluacionFichaMapper() {}

    public static EstadoEvaluacionFichaDomain toDomain(EstadoEvaluacionFichaEntity entity) {
        return EstadoEvaluacionFichaDomain.reconstruir(
                entity.id(),
                entity.evaluacionFichaPerfil(),
                EstadoEvaluacion.desde(entity.estadoEvaluacion()),
                entity.fechaActualizacion());
    }

    public static EstadoEvaluacionFichaEntity toEntity(EstadoEvaluacionFichaDomain aggregate) {
        return new EstadoEvaluacionFichaEntity(
                aggregate.getId(),
                aggregate.getEvaluacionFichaPerfilId(),
                aggregate.getEstadoEvaluacion().getId(),
                aggregate.getFechaActualizacion());
    }
}
