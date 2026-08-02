package com.arquisoft.fichas.infrastructure.estudiante.persistence;

import com.arquisoft.fichas.domain.estudiante.aggregate.EstudianteAggregate;
import org.springframework.stereotype.Component;

@Component
public class EstudianteMapper {

    public EstudianteAggregate toDomain(EstudianteEntity entity) {
        return EstudianteAggregate.reconstruir(
            entity.getId(),
            entity.getIdentificador(),
            entity.getNombre(),
            entity.getEmail()
        );
    }

    public EstudianteEntity toEntity(EstudianteAggregate aggregate) {
        return EstudianteEntity.builder()
            .id(aggregate.getId())
            .identificador(aggregate.getIdentificador())
            .nombre(aggregate.getNombre())
            .email(aggregate.getEmail())
            .build();
    }
}
