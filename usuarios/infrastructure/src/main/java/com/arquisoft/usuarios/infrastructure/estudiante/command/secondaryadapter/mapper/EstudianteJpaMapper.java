package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;

import java.time.Instant;

public final class EstudianteJpaMapper {

    private EstudianteJpaMapper() {}

    public static EstudianteEntity toEntity(EstudianteJpaEntity jpaEntity) {
        return new EstudianteEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static EstudianteJpaEntity toJpaEntity(EstudianteEntity entity) {
        return EstudianteJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
