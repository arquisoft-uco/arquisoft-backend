package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilCoordinadorJpaQueryEntity;

public final class EstadoFichaPerfilCoordinadorQueryMapper {

    private EstadoFichaPerfilCoordinadorQueryMapper() {}

    public static EstadoFichaPerfilReadModel toReadModel(EstadoFichaPerfilCoordinadorJpaQueryEntity entity) {
        return new EstadoFichaPerfilReadModel(
                entity.getEstadoId(),
                entity.getEstadoNombre(),
                entity.getFechaActualizacion());
    }
}
