package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository.EstadoFichaPerfilAsesorJpaQueryEntity;

public final class EstadoFichaPerfilAsesorQueryMapper {

    private EstadoFichaPerfilAsesorQueryMapper() {}

    public static EstadoFichaPerfilAsesorReadModel toReadModel(EstadoFichaPerfilAsesorJpaQueryEntity entity) {
        return new EstadoFichaPerfilAsesorReadModel(
                entity.getFichaPerfilId(),
                entity.getTituloProyecto(),
                entity.getEstadoId(),
                entity.getEstadoNombre(),
                entity.getFechaActualizacion());
    }
}
