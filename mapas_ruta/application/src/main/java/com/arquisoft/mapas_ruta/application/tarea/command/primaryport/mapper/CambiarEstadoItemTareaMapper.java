package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.CambiarEstadoItemTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;

public final class CambiarEstadoItemTareaMapper {

    private CambiarEstadoItemTareaMapper() {}

    public static CambioEstadoItemTareaDomain toDomain(CambiarEstadoItemTareaCommand command) {
        return CambioEstadoItemTareaDomain.crear(command.tarea(), command.estadoItem());
    }
}
