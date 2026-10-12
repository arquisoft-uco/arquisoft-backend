package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;

import java.time.Instant;

public final class AsesorFichaJpaMapper {

    private AsesorFichaJpaMapper() {}

    public static AsesorFichaEntity toEntity(AsesorFichaJpaEntity jpaEntity) {
        return new AsesorFichaEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static AsesorFichaJpaEntity toJpaEntity(AsesorFichaEntity entity) {
        return AsesorFichaJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
