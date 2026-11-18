package com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ResumenObservacionesRevisionEntity;
import com.arquisoft.fichas.domain.revisionitem.model.ObservacionesRevisionItem;

import java.util.UUID;

public final class ObservacionesRevisionItemMapper {

    private ObservacionesRevisionItemMapper() {}

    public static ObservacionesRevisionItem toDomain(UUID revisionItem, ResumenObservacionesRevisionEntity entity) {
        return new ObservacionesRevisionItem(revisionItem, entity.total(), entity.pendientes());
    }
}
