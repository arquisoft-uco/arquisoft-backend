package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilJpaQueryEntity;

public final class EstadoFichaPerfilQueryMapper {

    private EstadoFichaPerfilQueryMapper() {}

    public static EstadoFichaPerfilReadModel toReadModel(EstadoFichaPerfilJpaQueryEntity entity) {
        return new EstadoFichaPerfilReadModel(
                entity.getEstadoId(),
                entity.getEstadoNombre(),
                entity.getFechaActualizacion());
    }
}
