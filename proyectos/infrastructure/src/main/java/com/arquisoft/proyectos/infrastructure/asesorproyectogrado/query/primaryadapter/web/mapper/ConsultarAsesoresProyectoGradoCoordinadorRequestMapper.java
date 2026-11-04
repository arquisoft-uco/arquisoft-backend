package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.query.primaryport.model.ConsultarAsesoresProyectoGradoCoordinadorQuery;

import java.util.UUID;

public final class ConsultarAsesoresProyectoGradoCoordinadorRequestMapper {

    private ConsultarAsesoresProyectoGradoCoordinadorRequestMapper() {}

    public static ConsultarAsesoresProyectoGradoCoordinadorQuery toQuery(UUID proyectoGradoId, UUID coordinador) {
        return ConsultarAsesoresProyectoGradoCoordinadorQuery.crear(proyectoGradoId, coordinador);
    }
}
