package com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

public final class EstudianteMapper {

    private EstudianteMapper() {}

    public static EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(
            entity.getId(),
            entity.getIdentificador(),
            entity.getNombre(),
            entity.getEmail()
        );
    }

    public static EstudianteEntity toEntity(EstudianteDomain aggregate) {
        return EstudianteEntity.builder()
            .id(aggregate.getId())
            .identificador(aggregate.getIdentificador())
            .nombre(aggregate.getNombre())
            .email(aggregate.getEmail())
            .build();
    }
}
