package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity.ProyectoEstudianteAccesoEntity;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity.ProyectoEstudianteAccesoJpaEntity;

public final class ProyectoEstudianteAccesoJpaMapper {

    private ProyectoEstudianteAccesoJpaMapper() {}

    public static ProyectoEstudianteAccesoJpaEntity toJpaEntity(ProyectoEstudianteAccesoEntity entity) {
        return ProyectoEstudianteAccesoJpaEntity.builder()
                .proyecto(entity.proyecto())
                .estudiante(entity.estudiante())
                .activo(entity.activo())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }

    public static ProyectoEstudianteAccesoEntity toEntity(ProyectoEstudianteAccesoJpaEntity jpaEntity) {
        return new ProyectoEstudianteAccesoEntity(
                jpaEntity.getProyecto(),
                jpaEntity.getEstudiante(),
                jpaEntity.isActivo(),
                jpaEntity.getOcurridoEn());
    }
}
