package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.CambiarFechaFinMapaRutaCommand;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.CambiarFechaFinMapaRutaRequestDTO;

import java.util.UUID;

public final class CambiarFechaFinMapaRutaRequestMapper {

    private CambiarFechaFinMapaRutaRequestMapper() {}

    public static CambiarFechaFinMapaRutaCommand toCommand(
            CambiarFechaFinMapaRutaRequestDTO dto, UUID mapaRuta, UUID coordinador) {
        return CambiarFechaFinMapaRutaCommand.crear(mapaRuta, coordinador, dto.fechaFin());
    }
}
