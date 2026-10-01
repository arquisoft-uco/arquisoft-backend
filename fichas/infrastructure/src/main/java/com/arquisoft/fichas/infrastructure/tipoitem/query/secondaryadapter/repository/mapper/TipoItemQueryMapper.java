package com.arquisoft.fichas.infrastructure.tipoitem.query.secondaryadapter.repository.mapper;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.tipoitem.query.secondaryadapter.repository.TipoItemJpaQueryEntity;

public final class TipoItemQueryMapper {

    private TipoItemQueryMapper() {}

    public static TipoItemReadModel toReadModel(TipoItemJpaQueryEntity entity) {
        return new TipoItemReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion());
    }
}
