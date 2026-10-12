package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.CompaneroProyectoGradoReadModel;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel.EstudianteProyectoGradoReadModel;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.query.secondaryadapter.repository.EstudianteProyectoGradoJpaQueryEntity;

public final class EstudianteProyectoGradoQueryMapper {

    private EstudianteProyectoGradoQueryMapper() {}

    public static EstudianteProyectoGradoReadModel toReadModel(EstudianteProyectoGradoJpaQueryEntity entity) {
        return new EstudianteProyectoGradoReadModel(
                entity.getId(),
                entity.getProyectoGradoId(),
                entity.getEstudianteId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail(),
                entity.isVigente());
    }

    public static CompaneroProyectoGradoReadModel toCompaneroReadModel(EstudianteProyectoGradoJpaQueryEntity entity) {
        return new CompaneroProyectoGradoReadModel(
                entity.getEstudianteId(),
                entity.getIdentificador(),
                entity.getNombre(),
                entity.getEmail());
    }
}
