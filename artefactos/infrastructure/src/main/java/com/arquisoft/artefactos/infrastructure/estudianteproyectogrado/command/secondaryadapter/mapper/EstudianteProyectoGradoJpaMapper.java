package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity.PertenenciaProyectoGradoEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.EstudianteProyectoGradoCommandRepository.PertenenciaProyeccion;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class EstudianteProyectoGradoJpaMapper {

    private EstudianteProyectoGradoJpaMapper() {}

    public static PertenenciaProyectoGradoEntity toEntity(PertenenciaProyeccion proyeccion) {
        return new PertenenciaProyectoGradoEntity(proyeccion.artefacto(), proyeccion.estudiante());
    }

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoJpaEntity jpaEntity) {
        return new EstudianteProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEstudianteId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getDestituidoEn(), UtilFecha.VACIO));
    }

    public static EstudianteProyectoGradoJpaEntity toJpaEntity(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .estudianteId(entity.estudiante())
                .proyectoGradoId(entity.proyectoGrado())
                .ocurridoEn(entity.ocurridoEn())
                .destituidoEn(aColumna(entity.destituidoEn()))
                .build();
    }

    public static Instant aColumna(Instant destituidoEn) {
        return UtilFecha.VACIO.equals(destituidoEn) ? null : destituidoEn;
    }
}
