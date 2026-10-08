package com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;

import java.time.Instant;

public final class RepresentanteComiteJpaMapper {

    private RepresentanteComiteJpaMapper() {}

    public static RepresentanteComiteEntity toEntity(RepresentanteComiteJpaEntity jpaEntity) {
        return new RepresentanteComiteEntity(
                jpaEntity.getUsuarioId(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static RepresentanteComiteJpaEntity toJpaEntity(RepresentanteComiteEntity entity) {
        return RepresentanteComiteJpaEntity.builder()
                .usuarioId(entity.usuario())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
