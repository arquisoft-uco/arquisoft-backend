package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;

public final class EstadoEntregableProyectoGradoJpaMapper {

    private EstadoEntregableProyectoGradoJpaMapper() {}

    public static EstadoEntregableProyectoGradoEntity toEntity(EstadoEntregableProyectoGradoJpaEntity jpaEntity) {
        return new EstadoEntregableProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEntregableProyectoGrado(),
                jpaEntity.getEstadoEntregable(),
                jpaEntity.getFechaActualizacion());
    }

    public static EstadoEntregableProyectoGradoJpaEntity toJpaEntity(EstadoEntregableProyectoGradoEntity entity) {
        return EstadoEntregableProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .entregableProyectoGrado(entity.entregableProyectoGrado())
                .estadoEntregable(entity.estadoEntregable())
                .fechaActualizacion(entity.fechaActualizacion())
                .build();
    }
}
