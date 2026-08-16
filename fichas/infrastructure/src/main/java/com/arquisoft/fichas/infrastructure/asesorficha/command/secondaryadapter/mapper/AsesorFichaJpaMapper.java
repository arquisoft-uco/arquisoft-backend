package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;

import java.util.UUID;

public final class AsesorFichaJpaMapper {

    private AsesorFichaJpaMapper() {}

    public static AsesorFichaEntity toEntity(AsesorFichaJpaEntity jpaEntity) {
        return new AsesorFichaEntity(
                jpaEntity.getId(),
                jpaEntity.getIdentificador(),
                jpaEntity.getNombre(),
                jpaEntity.getEmail());
    }

    public static AsesorFichaJpaEntity toJpaEntity(AsesorFichaEntity entity) {
        return AsesorFichaJpaEntity.builder()
                .id(entity.id())
                .identificador(entity.identificador())
                .nombre(entity.nombre())
                .email(entity.email())
                .build();
    }

    public static AsesorFichaJpaEntity toReferencia(UUID asesorFicha) {
        return AsesorFichaJpaEntity.builder().id(asesorFicha).build();
    }
}
