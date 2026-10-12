package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.mapas_ruta.domain.maparuta.CambioFechaFinMapaRutaDomain;

public final class CambiarFechaFinMapaRutaMapper {

    private CambiarFechaFinMapaRutaMapper() {}

    public static CambioFechaFinMapaRutaDomain toDomain(CambiarFechaFinMapaRutaCommand command) {
        return CambioFechaFinMapaRutaDomain.crear(command.mapaRuta(), command.fechaFin(), command.coordinador());
    }
}
