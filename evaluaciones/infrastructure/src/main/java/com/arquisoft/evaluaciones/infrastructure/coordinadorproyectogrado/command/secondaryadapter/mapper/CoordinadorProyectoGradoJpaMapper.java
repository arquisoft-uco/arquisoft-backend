package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity.CoordinadorProyectoGradoEntity;
import com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.entity.CoordinadorProyectoGradoJpaEntity;

public final class CoordinadorProyectoGradoJpaMapper {

    private CoordinadorProyectoGradoJpaMapper() {}

    public static CoordinadorProyectoGradoEntity toEntity(CoordinadorProyectoGradoJpaEntity jpaEntity) {
        return new CoordinadorProyectoGradoEntity(
                jpaEntity.getProyectoGrado(),
                jpaEntity.getCoordinador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn());
    }

    public static CoordinadorProyectoGradoJpaEntity toJpaEntity(CoordinadorProyectoGradoEntity entity) {
        return CoordinadorProyectoGradoJpaEntity.builder()
                .proyectoGrado(entity.proyectoGrado())
                .coordinador(entity.coordinador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }
}
