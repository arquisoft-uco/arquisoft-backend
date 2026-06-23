package com.arquisoft.fichas.infrastructure.estudiante.persistence;

import com.arquisoft.fichas.domain.estudiante.aggregate.EstudianteAggregate;
import org.springframework.stereotype.Component;

@Component
public class EstudianteMapper {

    public EstudianteAggregate toDomain(EstudianteJpaEntity entity) {
        return EstudianteAggregate.reconstruir(
            entity.getId(),
            entity.getNombre(),
            entity.getEmail()
        );
    }

    public EstudianteJpaEntity toJpaEntity(EstudianteAggregate aggregate) {
        return EstudianteJpaEntity.builder()
            .id(aggregate.getId())
            .nombre(aggregate.getNombre())
            .email(aggregate.getEmail())
            .build();
    }
}
