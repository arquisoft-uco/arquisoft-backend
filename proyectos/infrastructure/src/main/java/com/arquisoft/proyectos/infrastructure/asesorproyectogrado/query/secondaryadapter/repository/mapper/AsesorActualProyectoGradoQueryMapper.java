package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel.AsesorActualProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.secondaryadapter.repository.AsesorActualProyectoGradoJpaQueryEntity;

public final class AsesorActualProyectoGradoQueryMapper {

    private AsesorActualProyectoGradoQueryMapper() {}

    public static AsesorActualProyectoGradoReadModel toReadModel(AsesorActualProyectoGradoJpaQueryEntity entity) {
        return new AsesorActualProyectoGradoReadModel(
                entity.getProyectoGradoId(),
                entity.getTituloProyecto(),
                entity.getAsesorId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail());
    }
}
