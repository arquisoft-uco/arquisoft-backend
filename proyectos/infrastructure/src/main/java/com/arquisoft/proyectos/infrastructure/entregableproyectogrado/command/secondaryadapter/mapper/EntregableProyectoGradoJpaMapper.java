package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;

public final class EntregableProyectoGradoJpaMapper {

    private EntregableProyectoGradoJpaMapper() {}

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoJpaEntity jpaEntity) {
        return new EntregableProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getVersionEntregable(),
                jpaEntity.getEstadoEntregableId(),
                jpaEntity.getOcurridoEn());
    }

    public static EntregableProyectoGradoJpaEntity toJpaEntity(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .proyectoGradoId(entity.proyectoGrado())
                .versionEntregable(entity.version())
                .estadoEntregableId(entity.estadoEntregable())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }
}
