package com.arquisoft.artefactos.infrastructure.observacion.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.command.primaryport.model.EliminarObservacionCommand;

import java.util.UUID;

public final class EliminarObservacionRequestMapper {

    private EliminarObservacionRequestMapper() {}

    public static EliminarObservacionCommand toCommand(UUID observacion, UUID asesor) {
        return EliminarObservacionCommand.crear(observacion, asesor);
    }
}
