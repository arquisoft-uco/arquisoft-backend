package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilDomain;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionFichaPerfilMapper {

    public EvaluacionFichaPerfilEntity toEntity(EvaluacionFichaPerfilDomain aggregate) {
        return EvaluacionFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .representanteComiteId(aggregate.getRepresentanteComiteId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .fechaCreacion(aggregate.getFechaCreacion())
                .build();
    }

    public EvaluacionFichaPerfilDomain toDomain(EvaluacionFichaPerfilEntity entity) {
        return EvaluacionFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getRepresentanteComiteId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion());
    }
}
