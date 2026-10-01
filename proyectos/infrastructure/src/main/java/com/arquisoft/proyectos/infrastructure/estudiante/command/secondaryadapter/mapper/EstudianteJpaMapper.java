package com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.proyectos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static EstudianteJpaEntity toJpaEntity(EstudianteEntity entity) {
        return EstudianteJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
