package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class RepresentanteComiteJpaMapper {

    private RepresentanteComiteJpaMapper() {}

    public static RepresentanteComiteEntity toEntity(RepresentanteComiteJpaEntity jpaEntity) {
        return new RepresentanteComiteEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static RepresentanteComiteJpaEntity toJpaEntity(RepresentanteComiteEntity entity) {
        return RepresentanteComiteJpaEntity.builder()
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
