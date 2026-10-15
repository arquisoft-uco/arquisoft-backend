package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.AsesorProyectoGradoJpaQueryEntity;

public final class AsesorProyectoGradoQueryMapper {

    private AsesorProyectoGradoQueryMapper() {}

    public static AsesorProyectoGradoReadModel toReadModel(AsesorProyectoGradoJpaQueryEntity entity) {
        return new AsesorProyectoGradoReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getAsesorId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.getEstadoAsesor(),
                entity.getEstadoAsesorNombre(),
                entity.getFechaAsignado(),
                entity.isVigente(),
                entity.isActual());
    }
}
