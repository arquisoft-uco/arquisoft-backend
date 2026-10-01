package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.command.secondaryadapter.entity.EvaluacionFichaPerfilJpaEntity;

import java.util.UUID;

public final class EvaluacionFichaPerfilJpaMapper {

    private EvaluacionFichaPerfilJpaMapper() {}

    public static EvaluacionFichaPerfilEntity toEntity(EvaluacionFichaPerfilJpaEntity jpaEntity) {
        return new EvaluacionFichaPerfilEntity(
                jpaEntity.getId(),
                jpaEntity.getRepresentanteComiteId(),
                jpaEntity.getFichaPerfilId(),
                jpaEntity.getFechaCreacion());
    }

    public static EvaluacionFichaPerfilJpaEntity toJpaEntity(EvaluacionFichaPerfilEntity entity) {
        return EvaluacionFichaPerfilJpaEntity.builder()
                .id(entity.id())
                .representanteComiteId(entity.representanteComiteId())
                .fichaPerfilId(entity.fichaPerfilId())
                .fechaCreacion(entity.fechaCreacion())
                .build();
    }

    public static EvaluacionFichaPerfilJpaEntity toReferencia(UUID id) {
        return EvaluacionFichaPerfilJpaEntity.builder().id(id).build();
    }
}
