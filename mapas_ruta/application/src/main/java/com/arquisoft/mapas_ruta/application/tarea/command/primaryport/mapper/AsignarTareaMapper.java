package com.arquisoft.mapas_ruta.application.tarea.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.primaryport.model.AsignarTareaCommand;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;

public final class AsignarTareaMapper {

    private AsignarTareaMapper() {}

    public static TareaDomain toDomain(AsignarTareaCommand command) {
        return TareaDomain.crear(command.mapaRuta(), command.descripcion(), command.asesor());
    }
}
