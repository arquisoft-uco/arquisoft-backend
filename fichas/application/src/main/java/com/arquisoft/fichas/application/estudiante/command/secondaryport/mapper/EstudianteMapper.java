package com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

public final class EstudianteMapper {

    private EstudianteMapper() {}

    public static EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(
            entity.id(),
            entity.identificador(),
            entity.nombre(),
            entity.email()
        );
    }

    public static EstudianteEntity toEntity(EstudianteDomain aggregate) {
        return new EstudianteEntity(
            aggregate.getId(),
            aggregate.getIdentificador(),
            aggregate.getNombre(),
            aggregate.getEmail()
        );
    }
}
