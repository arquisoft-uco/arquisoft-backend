package com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.PertenenciaRevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.model.PertenenciaRevisionItem;

public final class PertenenciaRevisionItemMapper {

    private PertenenciaRevisionItemMapper() {}

    public static PertenenciaRevisionItem toDomain(PertenenciaRevisionItemEntity entity) {
        return new PertenenciaRevisionItem(
                entity.fichaPerfilId(),
                entity.esPropietario(),
                EstadoRevision.desde(entity.estadoRevision()));
    }
}
