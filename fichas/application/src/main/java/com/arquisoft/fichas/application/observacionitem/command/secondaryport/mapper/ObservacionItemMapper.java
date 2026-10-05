package com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ObservacionItemEntity;
import com.arquisoft.fichas.domain.observacionitem.ObservacionItemDomain;

public final class ObservacionItemMapper {

    private ObservacionItemMapper() {}

    public static ObservacionItemEntity toEntity(ObservacionItemDomain observacionItem) {
        return new ObservacionItemEntity(
                observacionItem.getId(),
                observacionItem.getRevisionItem(),
                observacionItem.getObservacion(),
                observacionItem.getEstadoObservacionRevision().getId());
    }
}
