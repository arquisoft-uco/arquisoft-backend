package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.ModificarInformacionTareaRequestDTO;

import java.util.UUID;

public final class ModificarInformacionTareaRequestMapper {

    private ModificarInformacionTareaRequestMapper() {}

    public static ModificarInformacionTareaCommand toCommand(ModificarInformacionTareaRequestDTO dto, UUID tarea) {
        return ModificarInformacionTareaCommand.crear(tarea, dto.descripcion(), dto.asesor());
    }
}
