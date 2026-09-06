package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.domain.revisionitem.AgregacionRevisionItemDomain;
import com.arquisoft.fichas.domain.revisionitem.RevisionItemDomain;

public final class AgregarRevisionItemMapper {

    private AgregarRevisionItemMapper() {}

    public static AgregacionRevisionItemDomain toDomain(AgregarRevisionItemCommand command) {
        var revisionItem = RevisionItemDomain.crear(command.item());
        return AgregacionRevisionItemDomain.crear(revisionItem, command.asesorFicha());
    }
}
