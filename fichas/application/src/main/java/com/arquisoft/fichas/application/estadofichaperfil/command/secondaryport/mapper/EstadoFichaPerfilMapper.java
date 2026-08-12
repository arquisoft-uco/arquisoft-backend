package com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilEntity toEntity(
            EstadoFichaPerfilDomain aggregate,
            EstadoFichaEntity estadoFichaRef) {
        return EstadoFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfil())
                .estadoFicha(estadoFichaRef)
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilDomain toDomain(EstadoFichaPerfilEntity entity) {
        EstadoFicha estadoFicha = EstadoFicha.valueOf(entity.getEstadoFicha().getId());
        return EstadoFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                estadoFicha,
                entity.getFechaActualizacion()
        );
    }
}
