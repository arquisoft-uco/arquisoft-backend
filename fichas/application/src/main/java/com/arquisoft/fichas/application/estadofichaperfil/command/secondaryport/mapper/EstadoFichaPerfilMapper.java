package com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilEntity toEntity(EstadoFichaPerfilDomain aggregate) {
        return EstadoFichaPerfilEntity.builder()
                .id(aggregate.getId())
                .fichaPerfilId(aggregate.getFichaPerfil())
                .estadoFicha(EstadoFichaEntity.builder()
                        .id(aggregate.getEstadoFicha().getId())
                        .build())
                .fechaActualizacion(aggregate.getFechaActualizacion())
                .build();
    }

    public static EstadoFichaPerfilDomain toDomain(EstadoFichaPerfilEntity entity) {
        return EstadoFichaPerfilDomain.reconstruir(
                entity.getId(),
                entity.getFichaPerfilId(),
                EstadoFicha.desde(entity.getEstadoFicha().getId()),
                entity.getFechaActualizacion()
        );
    }
}
