package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;

public final class EntregableProyectoGradoJpaMapper {

    private EntregableProyectoGradoJpaMapper() {}

    public static EntregableProyectoGradoJpaEntity toJpaEntity(EntregableProyectoGradoEntity entity) {
        return EntregableProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .proyectoGrado(entity.proyectoGrado())
                .version(entity.version())
                .asesor(entity.asesor())
                .estadoEntregable(entity.estadoEntregable())
                .build();
    }

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoJpaEntity jpaEntity) {
        return new EntregableProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getProyectoGrado(),
                jpaEntity.getVersion(),
                jpaEntity.getAsesor(),
                jpaEntity.getEstadoEntregable());
    }
}
