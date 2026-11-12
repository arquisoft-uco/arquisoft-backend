package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;

import java.util.UUID;

public final class ConsultarMapaRutaProyectoGradoAsesorRequestMapper {

    private ConsultarMapaRutaProyectoGradoAsesorRequestMapper() {}

    public static ConsultarMapaRutaProyectoGradoAsesorQuery toQuery(UUID proyectoGrado) {
        return ConsultarMapaRutaProyectoGradoAsesorQuery.crear(proyectoGrado);
    }
}
