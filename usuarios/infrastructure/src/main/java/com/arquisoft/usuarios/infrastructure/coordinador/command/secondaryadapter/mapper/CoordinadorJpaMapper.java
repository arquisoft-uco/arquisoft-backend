package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;

import java.time.Instant;

public final class CoordinadorJpaMapper {

    private CoordinadorJpaMapper() {}

    public static CoordinadorEntity toEntity(CoordinadorJpaEntity jpaEntity) {
        return new CoordinadorEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static CoordinadorJpaEntity toJpaEntity(CoordinadorEntity entity) {
        return CoordinadorJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
