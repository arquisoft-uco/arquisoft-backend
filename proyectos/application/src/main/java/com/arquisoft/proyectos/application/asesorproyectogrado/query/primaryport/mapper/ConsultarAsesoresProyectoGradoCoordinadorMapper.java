package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorProyectoGradoCoordinadorCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;

public final class ConsultarAsesoresProyectoGradoCoordinadorMapper {

    private ConsultarAsesoresProyectoGradoCoordinadorMapper() {}

    public static AsesorProyectoGradoCoordinadorCriteria toCriteria(
            ConsultarAsesoresProyectoGradoCoordinadorQuery query) {
        return new AsesorProyectoGradoCoordinadorCriteria(query.proyectoGrado(), query.coordinador());
    }
}
