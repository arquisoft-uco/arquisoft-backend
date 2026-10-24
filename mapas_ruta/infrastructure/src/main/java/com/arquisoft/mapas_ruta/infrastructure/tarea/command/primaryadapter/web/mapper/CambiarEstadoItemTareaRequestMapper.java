package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.CambiarEstadoItemTareaCommand;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.CambiarEstadoItemTareaRequestDTO;

import java.util.UUID;

public final class CambiarEstadoItemTareaRequestMapper {

    private CambiarEstadoItemTareaRequestMapper() {}

    public static CambiarEstadoItemTareaCommand toCommand(CambiarEstadoItemTareaRequestDTO dto, UUID tarea) {
        return CambiarEstadoItemTareaCommand.crear(tarea, dto.estadoItem());
    }
}
