package com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.infrastructure.tarea.command.primaryadapter.web.dto.AsignarTareaRequestDTO;

import java.util.UUID;

public final class AsignarTareaRequestMapper {

    private AsignarTareaRequestMapper() {}

    public static AsignarTareaCommand toCommand(AsignarTareaRequestDTO dto, UUID mapaRuta) {
        return AsignarTareaCommand.crear(mapaRuta, dto.descripcion(), dto.asesor());
    }
}
