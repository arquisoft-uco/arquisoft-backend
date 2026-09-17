package com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;

public final class EstudianteMapper {

    private EstudianteMapper() {}

    public static EstudianteDomain toDomain(EstudianteEntity entity) {
        return EstudianteDomain.reconstruir(
                entity.id(),
                entity.identificador(),
                entity.nombre(),
                entity.email(),
                entity.ocurridoEn(),
                entity.eliminadoEn());
    }

    public static EstudianteEntity toEntity(EstudianteDomain estudiante) {
        return new EstudianteEntity(
                estudiante.getId(),
                estudiante.getIdentificador(),
                estudiante.getNombre(),
                estudiante.getEmail(),
                estudiante.getOcurridoEn(),
                estudiante.getEliminadoEn());
    }
}
