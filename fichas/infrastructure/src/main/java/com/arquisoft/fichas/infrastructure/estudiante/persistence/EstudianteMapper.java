package com.arquisoft.fichas.infrastructure.estudiante.persistence;

import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import org.springframework.stereotype.Component;

@Component
public class EstudianteMapper {

    public EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(
            entity.getId(),
            entity.getIdentificador(),
            entity.getNombre(),
            entity.getEmail()
        );
    }

    public EstudianteEntity toEntity(EstudianteDomain aggregate) {
        return EstudianteEntity.builder()
            .id(aggregate.getId())
            .identificador(aggregate.getIdentificador())
            .nombre(aggregate.getNombre())
            .email(aggregate.getEmail())
            .build();
    }
}
