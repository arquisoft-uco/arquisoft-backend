package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaEstudianteQuery;

import java.util.UUID;

public final class ConsultarMapaRutaEstudianteRequestMapper {

    private ConsultarMapaRutaEstudianteRequestMapper() {}

    public static ConsultarMapaRutaEstudianteQuery toQuery(UUID proyectoGrado) {
        return ConsultarMapaRutaEstudianteQuery.crear(proyectoGrado);
    }
}
