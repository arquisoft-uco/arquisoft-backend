package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

public final class EvaluacionFichaPerfilMapper {

    private EvaluacionFichaPerfilMapper() {}

    public static EvaluacionFichaPerfilEntity toEntity(EvaluacionFichaPerfilDomain aggregate) {
        return EvaluacionFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .representanteComiteId(aggregate.getRepresentanteComiteId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .fechaCreacion(aggregate.getFechaCreacion())
                .build();
    }

    public static EvaluacionFichaPerfilDomain toDomain(EvaluacionFichaPerfilEntity entity) {
        return EvaluacionFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getRepresentanteComiteId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion());
    }
}
