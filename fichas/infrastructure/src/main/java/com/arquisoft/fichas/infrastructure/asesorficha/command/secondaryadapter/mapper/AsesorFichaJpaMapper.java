package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;
import java.util.UUID;

public final class AsesorFichaJpaMapper {

    private AsesorFichaJpaMapper() {}

    public static AsesorFichaEntity toEntity(AsesorFichaJpaEntity jpaEntity) {
        return new AsesorFichaEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static AsesorFichaJpaEntity toJpaEntity(AsesorFichaEntity entity) {
        return AsesorFichaJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .eliminadoEn(aColumna(entity.eliminadoEn()))
                .build();
    }

    public static AsesorFichaJpaEntity toReferencia(UUID asesorFicha) {
        return AsesorFichaJpaEntity.builder().id(asesorFicha).build();
    }

    public static Instant aColumna(Instant eliminadoEn) {
        return UtilFecha.VACIO.equals(eliminadoEn) ? null : eliminadoEn;
    }
}
