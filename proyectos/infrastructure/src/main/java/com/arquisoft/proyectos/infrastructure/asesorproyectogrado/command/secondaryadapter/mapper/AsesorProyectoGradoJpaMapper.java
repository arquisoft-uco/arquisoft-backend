package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;

public final class AsesorProyectoGradoJpaMapper {

    private AsesorProyectoGradoJpaMapper() {}

    public static AsesorProyectoGradoEntity toEntity(AsesorProyectoGradoJpaEntity jpaEntity) {
        return new AsesorProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getAsesorId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getEstadoAsesorId(),
                jpaEntity.getFechaAsignado());
    }

    public static AsesorProyectoGradoJpaEntity toJpaEntity(AsesorProyectoGradoEntity entity) {
        return AsesorProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .asesorId(entity.asesor())
                .proyectoGradoId(entity.proyectoGrado())
                .estadoAsesorId(entity.estadoAsesor())
                .fechaAsignado(entity.fechaAsignado())
                .build();
    }
}
