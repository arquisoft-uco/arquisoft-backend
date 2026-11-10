package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.secondaryadapter.repository.EstadoEntregableProyectoGradoAccesoJpaQueryEntity;

public final class EstadoEntregableProyectoGradoAccesoQueryMapper {

    private EstadoEntregableProyectoGradoAccesoQueryMapper() {}

    public static EstadoEntregableProyectoGradoAccesoEntity toEntity(
            EstadoEntregableProyectoGradoAccesoJpaQueryEntity entity) {
        return new EstadoEntregableProyectoGradoAccesoEntity(
                entity.getId(),
                entity.getEntregableProyectoGradoId(),
                entity.getEstadoEntregableId(),
                entity.getFechaActualizacion());
    }
}
