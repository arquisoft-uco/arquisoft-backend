package com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaProyectoGradoCriteria;
import com.arquisoft.mapas_ruta.application.maparuta.query.primaryport.model.ConsultarMapaRutaProyectoGradoAsesorQuery;

public final class ConsultarMapaRutaProyectoGradoAsesorMapper {

    private ConsultarMapaRutaProyectoGradoAsesorMapper() {}

    public static MapaRutaProyectoGradoCriteria toCriteria(ConsultarMapaRutaProyectoGradoAsesorQuery query) {
        return new MapaRutaProyectoGradoCriteria(query.proyectoGrado());
    }
}
