package com.arquisoft.proyectos.infrastructure.proyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarDetalleProyectoGradoAsesorQuery;

import java.util.UUID;

public final class ConsultarDetalleProyectoGradoAsesorRequestMapper {

    private ConsultarDetalleProyectoGradoAsesorRequestMapper() {}

    public static ConsultarDetalleProyectoGradoAsesorQuery toQuery(UUID proyectoGradoId, UUID asesor) {
        return ConsultarDetalleProyectoGradoAsesorQuery.crear(proyectoGradoId, asesor);
    }
}
