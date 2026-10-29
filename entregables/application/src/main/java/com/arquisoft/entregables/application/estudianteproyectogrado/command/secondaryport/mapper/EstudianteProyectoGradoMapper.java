package com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

public final class EstudianteProyectoGradoMapper {

    private EstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoDomain.reconstruir(
                entity.id(), entity.estudiante(), entity.proyectoGrado(), entity.ocurridoEn(), entity.eliminadoEn());
    }

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoDomain vinculo) {
        return new EstudianteProyectoGradoEntity(
                vinculo.getId(), vinculo.getEstudiante(), vinculo.getProyectoGrado(), vinculo.getOcurridoEn(),
                vinculo.getEliminadoEn());
    }
}
