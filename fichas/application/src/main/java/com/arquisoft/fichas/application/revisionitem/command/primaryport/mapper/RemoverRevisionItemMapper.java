package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.RemoverRevisionItemCommand;
import com.arquisoft.fichas.domain.revisionitem.RemocionRevisionItemDomain;

public final class RemoverRevisionItemMapper {

    private RemoverRevisionItemMapper() {}

    public static RemocionRevisionItemDomain toDomain(RemoverRevisionItemCommand command) {
        return RemocionRevisionItemDomain.crear(command.revisionItem(), command.asesorFicha());
    }
}
