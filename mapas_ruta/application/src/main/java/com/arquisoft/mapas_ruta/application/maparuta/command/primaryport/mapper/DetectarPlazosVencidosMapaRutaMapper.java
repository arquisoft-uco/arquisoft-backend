package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.DetectarPlazosVencidosMapaRutaCommand;
import com.arquisoft.mapas_ruta.domain.maparuta.DeteccionPlazosVencidosMapaRutaDomain;

public final class DetectarPlazosVencidosMapaRutaMapper {

    private DetectarPlazosVencidosMapaRutaMapper() {}

    public static DeteccionPlazosVencidosMapaRutaDomain toDomain(DetectarPlazosVencidosMapaRutaCommand command) {
        return DeteccionPlazosVencidosMapaRutaDomain.crear(command.hoy());
    }
}
