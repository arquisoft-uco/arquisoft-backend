package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;

import java.util.UUID;

public final class RemoverInformacionTareaRequestMapper {

    private RemoverInformacionTareaRequestMapper() {}

    public static RemoverInformacionTareaCommand toCommand(UUID tarea) {
        return RemoverInformacionTareaCommand.crear(tarea);
    }
}
