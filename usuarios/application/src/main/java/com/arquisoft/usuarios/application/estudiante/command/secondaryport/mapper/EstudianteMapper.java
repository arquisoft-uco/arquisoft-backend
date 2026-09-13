package com.arquisoft.usuarios.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

public final class EstudianteMapper {

    private EstudianteMapper() {}

    public static EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(entity.usuario());
    }

    public static EstudianteEntity toEntity(EstudianteDomain estudiante) {
        return new EstudianteEntity(estudiante.getUsuario());
    }
}
