package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.entity.EntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.EntregableProyectoGradoAccesoJpaQueryEntity;

public final class EntregableProyectoGradoAccesoQueryMapper {

    private EntregableProyectoGradoAccesoQueryMapper() {}

    public static EntregableProyectoGradoAccesoEntity toEntity(EntregableProyectoGradoAccesoJpaQueryEntity entity) {
        return new EntregableProyectoGradoAccesoEntity(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getVersionEntregable().intValue(),
                entity.getAsesorId(),
                entity.getEstadoEntregableId());
    }
}
