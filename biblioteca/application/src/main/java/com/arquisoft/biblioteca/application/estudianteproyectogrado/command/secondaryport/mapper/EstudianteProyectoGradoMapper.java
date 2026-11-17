package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.mapper;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.AutorDeEntregableEntity;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.model.AutorDeEntregable;

public final class EstudianteProyectoGradoMapper {

    private EstudianteProyectoGradoMapper() {}

    public static EstudianteProyectoGradoDomain toDomain(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoDomain.reconstruir(
                entity.id(), entity.autor(), entity.proyectoGrado(), entity.ocurridoEn(), entity.eliminadoEn());
    }

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoDomain vinculo) {
        return new EstudianteProyectoGradoEntity(
                vinculo.getId(), vinculo.getAutor(), vinculo.getProyectoGrado(), vinculo.getOcurridoEn(),
                vinculo.getEliminadoEn());
    }

    public static AutorDeEntregable toAutorDeEntregable(AutorDeEntregableEntity entity) {
        return new AutorDeEntregable(entity.autor(), entity.vigente());
    }
}
