package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.EnviarCorreccionesRevisionItemCommand;
import com.arquisoft.fichas.domain.revisionitem.EnvioCorreccionesRevisionItemDomain;

public final class EnviarCorreccionesRevisionItemMapper {

    private EnviarCorreccionesRevisionItemMapper() {}

    public static EnvioCorreccionesRevisionItemDomain toDomain(EnviarCorreccionesRevisionItemCommand command) {
        return EnvioCorreccionesRevisionItemDomain.crear(command.revisionItem(), command.estudiante());
    }
}
