package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilEntity toEntity(
            EstadoFichaPerfilAggregate aggregate,
            EstadoFichaEntity estadoFichaRef) {
        return EstadoFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfilId())
                .estadoFicha(estadoFichaRef)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilAggregate toDomain(EstadoFichaPerfilEntity entity) {
        EstadoFicha estadoFicha = EstadoFicha.valueOf(entity.getEstadoFicha().getId());
        return EstadoFichaPerfilAggregate.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                estadoFicha,
                entity.getFechaActualizacion()
        );
    }
}
