package com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.observacionitem.model.ContextoObservacionItem;

public final class ContextoObservacionItemMapper {

    private ContextoObservacionItemMapper() {}

    public static ContextoObservacionItem toDomain(ContextoObservacionItemEntity entity) {
        return new ContextoObservacionItem(
                entity.revisionItem(),
                EstadoRevision.desde(entity.estadoRevision()),
                entity.fichaPerfil(),
                entity.asesorFicha());
    }
}
