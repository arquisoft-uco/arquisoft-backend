package com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.estudianteproyectogrado.query.criteria.CompaneroProyectoGradoCriteria;
import com.arquisoft.proyectos.application.estudianteproyectogrado.query.primaryport.model.ConsultarCompanerosProyectoGradoQuery;

public final class ConsultarCompanerosProyectoGradoMapper {

    private ConsultarCompanerosProyectoGradoMapper() {}

    public static CompaneroProyectoGradoCriteria toCriteria(ConsultarCompanerosProyectoGradoQuery query) {
        return new CompaneroProyectoGradoCriteria(query.proyectoGrado(), query.estudiante());
    }
}
