package com.arquisoft.artefactos.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.artefactos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.artefactos.domain.estudiante.EstudianteDomain;

public final class EstudianteMapper {

    private EstudianteMapper() {}

    public static EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.eliminadoEn());
    }
}
