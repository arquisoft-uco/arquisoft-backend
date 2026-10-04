package com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.fichas.domain.revisionitem.model.AsesoriaRevisionItem;

public final class AsesoriaRevisionItemMapper {

    private AsesoriaRevisionItemMapper() {}

    public static AsesoriaRevisionItem toDomain(AsesoriaRevisionItemEntity entity) {
        return new AsesoriaRevisionItem(
                entity.fichaPerfilId(),
                entity.asesorFichaId(),
                EstadoRevision.desde(entity.estadoRevision()));
    }
}
