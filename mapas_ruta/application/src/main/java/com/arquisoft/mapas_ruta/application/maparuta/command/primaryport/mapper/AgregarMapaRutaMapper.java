package com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.primaryport.model.AgregarMapaRutaCommand;
import com.arquisoft.mapas_ruta.domain.maparuta.AgregacionMapaRutaDomain;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;

public final class AgregarMapaRutaMapper {

    private AgregarMapaRutaMapper() {}

    public static AgregacionMapaRutaDomain toDomain(AgregarMapaRutaCommand command) {
        var mapaRuta = MapaRutaDomain.crear(command.proyectoGrado(), command.fechaInicio(), command.fechaFin());
        return AgregacionMapaRutaDomain.crear(mapaRuta, command.coordinador());
    }
}
