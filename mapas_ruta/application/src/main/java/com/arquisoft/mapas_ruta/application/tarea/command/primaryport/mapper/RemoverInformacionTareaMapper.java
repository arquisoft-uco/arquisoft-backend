package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.RemoverInformacionTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;

public final class RemoverInformacionTareaMapper {

    private RemoverInformacionTareaMapper() {}

    public static RemocionTareaDomain toDomain(RemoverInformacionTareaCommand command) {
        return RemocionTareaDomain.crear(command.tarea());
    }
}
