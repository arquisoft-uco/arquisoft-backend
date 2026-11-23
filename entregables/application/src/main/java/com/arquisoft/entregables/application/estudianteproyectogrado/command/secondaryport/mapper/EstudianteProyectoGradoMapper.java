package com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;

public final class EstudianteProyectoGradoMapper {

    private EstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoDomain.reconstruir(
                entity.id(), entity.estudiante(), entity.proyectoGrado(), entity.nombre(), entity.email(),
                entity.ocurridoEn(), entity.eliminadoEn());
    }

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoDomain vinculo) {
        return new EstudianteProyectoGradoEntity(
                vinculo.getId(), vinculo.getEstudiante(), vinculo.getProyectoGrado(), vinculo.getNombre(),
                vinculo.getEmail(), vinculo.getOcurridoEn(), vinculo.getEliminadoEn());
    }

    public static ContactoEstudiante toContacto(EstudianteProyectoGradoEntity entity) {
        return new ContactoEstudiante(entity.estudiante(), entity.nombre(), entity.email());
    }
}
