package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresActualesProyectoGradoEstudianteQuery;

import java.util.UUID;

public final class ConsultarAsesoresActualesProyectoGradoEstudianteRequestMapper {

    private ConsultarAsesoresActualesProyectoGradoEstudianteRequestMapper() {}

    public static ConsultarAsesoresActualesProyectoGradoEstudianteQuery toQuery(UUID estudiante) {
        return ConsultarAsesoresActualesProyectoGradoEstudianteQuery.crear(estudiante);
    }
}
