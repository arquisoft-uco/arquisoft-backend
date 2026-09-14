package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;

public final class CoordinadorJpaMapper {

    private CoordinadorJpaMapper() {}

    public static CoordinadorEntity toEntity(CoordinadorJpaEntity jpaEntity) {
        return new CoordinadorEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail(),
                jpaEntity.getOcurridoEn());
    }

    public static CoordinadorJpaEntity toJpaEntity(CoordinadorEntity entity) {
        return CoordinadorJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .ocurridoEn(entity.ocurridoEn())
                .build();
    }
}
