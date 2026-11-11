package com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estadoproyectogrado.query.criteria.HistorialEstadosProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estadoproyectogrado.query.primaryport.model.ConsultarHistorialEstadosProyectoGradoQuery;

public final class ConsultarHistorialEstadosProyectoGradoMapper {

    private ConsultarHistorialEstadosProyectoGradoMapper() {}

    public static HistorialEstadosProyectoGradoCriteria toCriteria(ConsultarHistorialEstadosProyectoGradoQuery query) {
        return new HistorialEstadosProyectoGradoCriteria(query.proyectoGrado(), query.usuario());
    }
}
