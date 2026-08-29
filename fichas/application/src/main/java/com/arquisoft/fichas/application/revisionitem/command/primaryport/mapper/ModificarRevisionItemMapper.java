package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.domain.revisionitem.ModificacionRevisionItemDomain;

public final class ModificarRevisionItemMapper {

    private ModificarRevisionItemMapper() {}

    public static ModificacionRevisionItemDomain toDomain(ModificarRevisionItemCommand command) {
        return ModificacionRevisionItemDomain.crear(
                command.item(), command.estadoRevision(), command.asesorFicha());
    }
}
