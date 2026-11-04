package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.ModificarInformacionTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.ModificacionTareaDomain;

public final class ModificarInformacionTareaMapper {

    private ModificarInformacionTareaMapper() {}

    public static ModificacionTareaDomain toDomain(ModificarInformacionTareaCommand command) {
        return ModificacionTareaDomain.crear(command.tarea(), command.descripcion(), command.asesor());
    }
}
