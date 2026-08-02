package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence;

import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionRepository;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaMapper {

    private final EstadoEvaluacionRepository estadoEvaluacionRepository;

    public EstadoEvaluacionFichaAggregate toDomain(EstadoEvaluacionFichaEntity entity) {
        return EstadoEvaluacionFichaAggregate.reconstruir(
                entity.getId(),
                entity.getEvaluacionFichaPerfil().getId(),
                EstadoEvaluacion.valueOf(entity.getEstadoEvaluacion().getId()),
                entity.getFechaActualizacion());
    }

    public EstadoEvaluacionFichaEntity toEntity(
            EstadoEvaluacionFichaAggregate aggregate,
            EvaluacionFichaPerfilEntity evaluacionFichaPerfilRef) {

        EstadoEvaluacionEntity estadoEvaluacionRef = estadoEvaluacionRepository
                .getReferenceById(aggregate.getEstadoEvaluacion().name());

        return EstadoEvaluacionFichaEntity.builder()
                .id(aggregate.getId())
                .evaluacionFichaPerfil(evaluacionFichaPerfilRef)
                .estadoEvaluacion(estadoEvaluacionRef)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }
}
