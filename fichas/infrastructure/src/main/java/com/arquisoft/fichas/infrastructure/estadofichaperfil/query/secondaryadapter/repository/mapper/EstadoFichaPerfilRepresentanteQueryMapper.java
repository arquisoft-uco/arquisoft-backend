package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilRepresentanteJpaQueryEntity;

public final class EstadoFichaPerfilRepresentanteQueryMapper {

    private EstadoFichaPerfilRepresentanteQueryMapper() {}

    public static EstadoFichaPerfilReadModel toReadModel(EstadoFichaPerfilRepresentanteJpaQueryEntity entity) {
        return new EstadoFichaPerfilReadModel(
                entity.getEstadoId(),
                entity.getEstadoNombre(),
                entity.getFechaActualizacion());
    }
}
