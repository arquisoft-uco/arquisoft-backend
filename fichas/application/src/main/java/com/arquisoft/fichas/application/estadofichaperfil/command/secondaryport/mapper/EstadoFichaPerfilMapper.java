package com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;

public final class EstadoFichaPerfilMapper {

    private EstadoFichaPerfilMapper() {}

    public static EstadoFichaPerfilEntity toEntity(EstadoFichaPerfilDomain aggregate) {
        return new EstadoFichaPerfilEntity(
                aggregate.getId(),
                aggregate.getFichaPerfil(),
                aggregate.getEstadoFicha().getId(),
                aggregate.getFechaActualizacion());
    }

    public static EstadoFichaPerfilDomain toDomain(EstadoFichaPerfilEntity entity) {
        return EstadoFichaPerfilDomain.reconstruir(
                entity.id(),
                entity.fichaPerfilId(),
                EstadoFicha.desde(entity.estadoFicha()),
                entity.fechaActualizacion()
        );
    }
}
