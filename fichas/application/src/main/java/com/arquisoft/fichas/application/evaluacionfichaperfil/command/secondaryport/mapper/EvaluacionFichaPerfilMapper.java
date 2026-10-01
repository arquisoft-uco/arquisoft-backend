package com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.secondaryport.entity.EvaluacionFichaPerfilEntity;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.EvaluacionFichaPerfilDomain;

public final class EvaluacionFichaPerfilMapper {

    private EvaluacionFichaPerfilMapper() {}

    public static EvaluacionFichaPerfilEntity toEntity(EvaluacionFichaPerfilDomain aggregate) {
        return new EvaluacionFichaPerfilEntity(
                aggregate.getId(),
                aggregate.getRepresentanteComiteId(),
                aggregate.getFichaPerfilId(),
                aggregate.getFechaCreacion());
    }

    public static EvaluacionFichaPerfilDomain toDomain(EvaluacionFichaPerfilEntity entity) {
        return EvaluacionFichaPerfilDomain.reconstruir(
                entity.id(),
                entity.representanteComiteId(),
                entity.fichaPerfilId(),
                entity.fechaCreacion());
    }
}
