package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilObjeto;

import java.time.Instant;

public final class AsesorJpaMapper {

    private AsesorJpaMapper() {}

    public static AsesorEntity toEntity(AsesorJpaEntity jpaEntity) {
        return new AsesorEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn(),
                UtilObjeto.aplicarPorDefecto(jpaEntity.getEliminadoEn(), UtilFecha.VACIO));
    }

    public static AsesorJpaEntity toJpaEntity(AsesorEntity entity) {
        return AsesorJpaEntity.builder()
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
