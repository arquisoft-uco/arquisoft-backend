package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaEstudianteCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaEstudianteQuery;

public final class ConsultarMapaRutaEstudianteMapper {

    private ConsultarMapaRutaEstudianteMapper() {}

    public static MapaRutaEstudianteCriteria toCriteria(ConsultarMapaRutaEstudianteQuery query) {
        return new MapaRutaEstudianteCriteria(query.estudiante());
    }
}
