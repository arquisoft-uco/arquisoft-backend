package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaMapper {

    private final EstadoEvaluacionJpaRepository estadoEvaluacionJpaRepository;

    public EstadoEvaluacionFichaAggregate toDomain(EstadoEvaluacionFichaJpaEntity entity) {
        return EstadoEvaluacionFichaAggregate.reconstruir(
                entity.getId(),
                entity.getEvaluacionFichaPerfil().getId(),
                EstadoEvaluacion.valueOf(entity.getEstadoEvaluacion().getId()),
                entity.getFechaActualizacion());
    }

    public EstadoEvaluacionFichaJpaEntity toEntity(
            EstadoEvaluacionFichaAggregate aggregate,
            EvaluacionFichaPerfilJpaEntity evaluacionFichaPerfilRef) {

        EstadoEvaluacionJpaEntity estadoEvaluacionRef = estadoEvaluacionJpaRepository
                .getReferenceById(aggregate.getEstadoEvaluacion().name());

        return EstadoEvaluacionFichaJpaEntity.builder()
                .id(aggregate.getId())
                .evaluacionFichaPerfil(evaluacionFichaPerfilRef)
                .estadoEvaluacion(estadoEvaluacionRef)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }
}
