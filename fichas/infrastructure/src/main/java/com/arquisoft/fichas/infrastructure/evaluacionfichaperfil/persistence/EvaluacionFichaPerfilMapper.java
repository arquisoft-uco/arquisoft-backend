package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence;

import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import org.springframework.stereotype.Component;

@Component
public class EvaluacionFichaPerfilMapper {

    public EvaluacionFichaPerfilJpaEntity toJpaEntity(EvaluacionFichaPerfilAggregate aggregate) {
        return EvaluacionFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .representanteComiteId(aggregate.getRepresentanteComiteId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .fechaCreacion(aggregate.getFechaCreacion())
                .build();
    }

    public EvaluacionFichaPerfilAggregate toDomain(EvaluacionFichaPerfilJpaEntity entity) {
        return EvaluacionFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getRepresentanteComiteId(),
                entity.getFichaPerfilId(),
                entity.getFechaCreacion());
    }
}
