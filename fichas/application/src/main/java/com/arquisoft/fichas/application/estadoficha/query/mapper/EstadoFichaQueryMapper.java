package com.arquisoft.fichas.application.estadoficha.query.mapper;

import com.arquisoft.fichas.application.estadoficha.command.secondaryport.entity.EstadoFichaEntity;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;

public final class EstadoFichaQueryMapper {

    private EstadoFichaQueryMapper() {}

    public static EstadoFichaReadModel toReadModel(EstadoFichaEntity entity) {
        return new EstadoFichaReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
