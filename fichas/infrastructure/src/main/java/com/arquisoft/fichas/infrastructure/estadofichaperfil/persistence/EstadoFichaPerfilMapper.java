package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaEntity;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilJpaEntity toJpaEntity(
            EstadoFichaPerfilAggregate aggregate,
            EstadoFichaJpaEntity estadoFichaRef) {
        return EstadoFichaPerfilJpaEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .estadoFicha(estadoFichaRef)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilAggregate toDomain(EstadoFichaPerfilJpaEntity entity) {
        EstadoFicha estadoFicha = EstadoFicha.valueOf(entity.getEstadoFicha().getId());
        return EstadoFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                estadoFicha,
                entity.getFechaActualizacion()
        );
    }
}
