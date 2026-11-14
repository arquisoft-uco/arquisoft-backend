package com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.criteria.AsesorActualProyectoGradoEstudianteCriteria;
import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;

public final class ConsultarAsesoresActualesProyectoGradoEstudianteMapper {

    private ConsultarAsesoresActualesProyectoGradoEstudianteMapper() {}

    public static AsesorActualProyectoGradoEstudianteCriteria toCriteria(
            ConsultarAsesoresActualesProyectoGradoEstudianteQuery query) {
        return new AsesorActualProyectoGradoEstudianteCriteria(query.estudiante());
    }
}
