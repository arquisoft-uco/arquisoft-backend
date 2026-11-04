package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class EstudianteProyectoGradoJpaMapper {

    private EstudianteProyectoGradoJpaMapper() {}

    public static EstudianteProyectoGradoEntity toEntity(EstudianteProyectoGradoJpaEntity jpaEntity) {
        return new EstudianteProyectoGradoEntity(
                jpaEntity.getId(),
                jpaEntity.getEstudianteId(),
                jpaEntity.getProyectoGradoId(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static EstudianteProyectoGradoJpaEntity toJpaEntity(EstudianteProyectoGradoEntity entity) {
        return EstudianteProyectoGradoJpaEntity.builder()
                .id(entity.id())
                .estudianteId(entity.estudiante())
                .proyectoGradoId(entity.proyectoGrado())
                .ocurridoEn(entity.ocurridoEn())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    private static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
