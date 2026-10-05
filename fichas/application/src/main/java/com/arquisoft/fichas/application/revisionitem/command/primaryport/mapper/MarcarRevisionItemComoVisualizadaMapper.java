package com.arquisoft.fichas.application.revisionitem.command.primaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.MarcarRevisionItemComoVisualizadaCommand;
import com.arquisoft.fichas.domain.revisionitem.VisualizacionRevisionItemDomain;

public final class MarcarRevisionItemComoVisualizadaMapper {

    private MarcarRevisionItemComoVisualizadaMapper() {}

    public static VisualizacionRevisionItemDomain toDomain(MarcarRevisionItemComoVisualizadaCommand command) {
        return VisualizacionRevisionItemDomain.crear(command.revisionItem(), command.estudiante());
    }
}
