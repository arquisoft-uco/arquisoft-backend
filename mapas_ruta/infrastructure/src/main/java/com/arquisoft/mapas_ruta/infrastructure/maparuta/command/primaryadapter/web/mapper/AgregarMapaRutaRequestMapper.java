package com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.command.primaryadapter.web.dto.AgregarMapaRutaRequestDTO;

import java.util.UUID;

public final class AgregarMapaRutaRequestMapper {

    private AgregarMapaRutaRequestMapper() {}

    public static AgregarMapaRutaCommand toCommand(AgregarMapaRutaRequestDTO dto, UUID coordinador) {
        return AgregarMapaRutaCommand.crear(dto.proyectoGrado(), coordinador, dto.fechaInicio(), dto.fechaFin());
    }
}
