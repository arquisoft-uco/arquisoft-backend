package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;

import java.util.UUID;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilJpaEntity toJpaEntity(EstadoFichaPerfilAggregate aggregate, UUID estadoFichaId) {
        return EstadoFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .estadoFichaId(estadoFichaId)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilAggregate toDomain(EstadoFichaPerfilJpaEntity entity, String estadoFichaNombre) {
        EstadoFicha estadoFicha = EstadoFicha.desdeCatalogo(estadoFichaNombre);
        return EstadoFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                estadoFicha,
                entity.getFechaActualizacion()
        );
    }
}
