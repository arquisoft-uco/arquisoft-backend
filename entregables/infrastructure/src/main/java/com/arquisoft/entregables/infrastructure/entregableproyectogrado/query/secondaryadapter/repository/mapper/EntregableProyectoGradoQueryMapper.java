package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository.EntregableProyectoGradoJpaQueryEntity;

public final class EntregableProyectoGradoQueryMapper {

    private EntregableProyectoGradoQueryMapper() {}

    public static EntregableProyectoGradoReadModel toReadModel(EntregableProyectoGradoJpaQueryEntity entity) {
        return new EntregableProyectoGradoReadModel(
                entity.getId(),
                entity.getVersionEntregable().intValue(),
                new EntregableProyectoGradoReadModel.ProyectoGradoResumen(
                        entity.getProyectoGradoId(),
                        entity.getProyectoGradoNombre()),
                new EntregableProyectoGradoReadModel.AsesorResumen(
                        entity.getAsesorId(),
                        entity.getAsesorNombre(),
                        entity.getAsesorEmail()),
                new EntregableProyectoGradoReadModel.EstadoEntregableResumen(
                        entity.getEstadoId(),
                        entity.getEstadoNombre()));
    }
}
