package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;

public final class AsesorJpaMapper {

    private AsesorJpaMapper() {}

    public static AsesorEntity toEntity(AsesorJpaEntity jpaEntity) {
        return new AsesorEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn());
    }

    public static AsesorJpaEntity toJpaEntity(AsesorEntity entity) {
        return AsesorJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }
}
